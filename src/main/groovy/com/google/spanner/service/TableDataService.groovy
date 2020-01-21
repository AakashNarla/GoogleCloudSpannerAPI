package com.google.spanner.service

import com.google.cloud.spanner.*
import com.google.cloud.spanner.TransactionRunner.TransactionCallable
import com.google.common.base.Joiner
import com.google.spanner.exception.ResourceNotFoundException
import com.google.spanner.util.LoadCredentialsAPI
import com.google.spanner.util.TableDataBuilder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

@Service
@Slf4j
class TableDataService {

    @Autowired
    LoadCredentialsAPI loadCredentialsAPI

    private Spanner spanner

    DatabaseClient getDatabaseClient(String url, String instance, String database) {
        DatabaseClient dbClient = null
        try {
            String project = loadCredentialsAPI.getProjectFromCredentials(url)
            log.debug("Project Id : {}", project)
            if (project && instance && database) {
                spanner = loadCredentialsAPI.getSpanner(url)
                DatabaseId db = DatabaseId.of(project, instance, database)
                dbClient = spanner.getDatabaseClient(db)
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), e?.detailedMessage?.toString() ?: e?.message, new Exception())
        }
        return dbClient
    }

    /** unprotected blind write. */
    String writeAtLeastOnce(String url, String instanceId, String databaseId, String table, List<Map> insertDataList) {
        DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
        try {
            def mutations = TableDataBuilder.createMutationList(table, insertDataList)

            if (mutations && mutations.size() > 0) {
                dbClient.write(mutations as Iterable<Mutation>)
                return "Successfully Inserted data"
            } else {
                return "Something went wrong while inserting data"
            }
        } catch (e) {
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
    }

    int upsertUsingDml(String url, String instanceId, String databaseId, String query) {
        DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
        long rowCount = -1
        try {
            dbClient.readWriteTransaction().run(
                    new TransactionCallable<Void>() {
                        @Override
                         Void run(TransactionContext transaction) throws Exception {
                            rowCount = transaction.executeUpdate(Statement.of(query))
                            log.info("Record inserted {}", rowCount)
                            return null
                        }
                    })
        } catch (e) {
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return rowCount.intValue()
    }


    /**
     * Single use with timestamp bound.
     */
    List<Map> selectSemiQuery(String url, String instanceId, String databaseId, String table, String whereCondition, String fieldString) {
        DatabaseClient dbClient
        List<Map> finalList = new ArrayList()
        try {
            def fields = fieldString.tokenize(",")*.trim()
            fields = fields.unique().collect { it.toSet().join() }

            dbClient = getDatabaseClient(url, instanceId, databaseId)

            Joiner joiner = Joiner.on(',')
            Statement query = Statement.newBuilder("SELECT ")
                    .append(joiner.join(fields))
                    .append(" FROM ")
                    .append(table)
                    .append(whereCondition ? " where " + whereCondition : "")
                    .build()
            ResultSet resultSet = dbClient.singleUse(TimestampBound.ofMaxStaleness(1, TimeUnit.MINUTES)).executeQuery(query)
            while (resultSet.next()) {
                def outputMap = [:]
                for (String field : fields) {
                    outputMap.put(field, getObjectFromQuery(resultSet, 0, field))
                }
                finalList.add(outputMap)
            }
        } catch (e) {
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return finalList
    }

    /**
     * Single use with timestamp bound.
     */
    double getSelectCount(String url, String instanceId, String databaseId, String table, String whereCondition) {
        DatabaseClient dbClient
        List<Map> finalList = new ArrayList()
        long count = 0
        try {
            dbClient = getDatabaseClient(url, instanceId, databaseId)

            Statement query = Statement.newBuilder("SELECT ")
                    .append(" COUNT(*) AS rowcount ")
                    .append(" FROM ")
                    .append(table)
                    .append(whereCondition ? " where " + whereCondition : "")
                    .build()
            ResultSet resultSet = dbClient.singleUse(TimestampBound.ofMaxStaleness(1, TimeUnit.MINUTES)).executeQuery(query)
            while (resultSet.next()) {
                count = resultSet.getLong("rowcount")
            }
        } catch (e) {
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return count
    }


    // [START spanner_delete_data]
    boolean deleteData(String url, String instanceId, String databaseId, String tableName, List<String> pKeyList) {
        DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
        boolean isSuccess = false
        try {
            def mutations = new ArrayList<>()
            if (pKeyList && pKeyList.size() > 0) {

                for (String pKey in pKeyList) {
                    mutations.add(Mutation.delete(tableName, KeySet.singleKey(Key.newBuilder().append(pKey).build())))
                }

                dbClient.write(mutations as Iterable<Mutation>)
                isSuccess = true
                log.info("Records deleted.\n")
            }

        } catch (e) {
            isSuccess = false
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return isSuccess
    }


    // [START spanner_query_data]
    List<Map> query(String url, String instanceId, String databaseId, String query) {
        DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
        List<Map> finalList = new ArrayList()
        try {

            ResultSet resultSet = dbClient
                    .singleUse() // Execute a single read or query against Cloud Spanner.
                    .executeQuery(Statement.of(query))
            while (resultSet.next()) {
                def outputMap = [:]
                int count = resultSet.getColumnCount()
                Struct struct = resultSet.getCurrentRowAsStruct()
                for (int i = 0 ; i < count; i++) {
                    String columnName = struct.type.structFields.get(i).name
                    outputMap.put(columnName, getObjectFromQuery(resultSet, i, columnName))
                }
                finalList.add(outputMap)

            }
        } catch (e) {
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return finalList
    }

    static Object getObjectFromQuery(ResultSet resultSet, int i, String columnName) {
        Type type = resultSet.getColumnType(columnName ?: i )
        Object returnObject
        switch (type) {
            case Type.TYPE_INT64:
                returnObject = resultSet.getLong(columnName ?: i )
                break
            case Type.TYPE_FLOAT64:
                returnObject = resultSet.getLong(columnName ?: i )
                break
            case Type.TYPE_STRING:
                returnObject = resultSet.getString(columnName ?: i )
                break
            case Type.TYPE_TIMESTAMP:
                returnObject = resultSet.getTimestamp(columnName ?: i )
                break
            case Type.TYPE_DATE:
                returnObject = resultSet.getDate(columnName ?: i )
                break
            case Type.TYPE_BYTES:
                returnObject = resultSet.getBytes(columnName ?: i )
                break
            case Type.TYPE_ARRAY_BOOL:
                returnObject = resultSet.getBooleanArray(columnName ?: i )
                break
            case Type.TYPE_ARRAY_BYTES:
                returnObject = resultSet.getBytesList(columnName ?: i )
                break
            case Type.TYPE_ARRAY_DATE:
                returnObject = resultSet.getDateList(columnName ?: i )
                break
            case Type.TYPE_ARRAY_TIMESTAMP:
                returnObject = resultSet.getTimestampList(columnName ?: i )
                break
            case Type.TYPE_ARRAY_FLOAT64:
                returnObject = resultSet.getLongArray(columnName ?: i )
                break
            case Type.TYPE_ARRAY_INT64:
                returnObject = resultSet.getDoubleList(columnName ?: i )
                break
            case Type.TYPE_ARRAY_STRING:
                returnObject = resultSet.getStringList(columnName ?: i )
                break

            default:
                returnObject = null
                break
        }
        return returnObject
    }

    /**
     *  ALl Delete Related Query
     */

    // [START spanner_delete_data]
    boolean truncateTable(String url, String instanceId, String databaseId, String tableName) {
        DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
        boolean isSuccess = false
        try {

            def mutations = new ArrayList<>()
            if (tableName) {
                mutations.add(Mutation.delete(tableName, KeySet.all()))
                dbClient.write(mutations as Iterable<Mutation>)
                isSuccess = true
                log.info("Records deleted.\n")
            }
        } catch (e) {
            isSuccess = false
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return isSuccess
    }

    // [START spanner_dml_standard_delete]
    int deleteUsingDml(String url, String instanceId, String databaseId, String query) {
        DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
        long rowCount = -1
        try {
            dbClient.readWriteTransaction().run(
                    new TransactionCallable<Void>() {
                        @Override
                         Void run(TransactionContext transaction) throws Exception {
                            //String sql = "DELETE FROM Singers WHERE FirstName = 'Alice'"
                            rowCount = transaction.executeUpdate(Statement.of(query))
                            log.info("Record deleted : {}", rowCount)
                            return null
                        }
                    })
        } catch (e) {
            log.error("Unexpected Error : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return rowCount
    }
}

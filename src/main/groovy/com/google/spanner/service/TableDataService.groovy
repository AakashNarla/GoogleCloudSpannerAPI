package com.google.spanner.service


import java.sql.ResultSetMetaData
import java.util.concurrent.TimeUnit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.Timestamp
import com.google.cloud.spanner.DatabaseClient
import com.google.cloud.spanner.DatabaseId
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions
import com.google.cloud.spanner.Statement
import com.google.cloud.spanner.Struct
import com.google.cloud.spanner.Key
import com.google.cloud.spanner.KeySet
import com.google.cloud.spanner.TimestampBound
import com.google.cloud.spanner.TransactionContext
import com.google.cloud.spanner.TransactionRunner.TransactionCallable
import com.google.common.base.Joiner
import com.google.cloud.spanner.Type
import com.google.spanner.exception.ResourceNotFoundException
import com.google.spanner.util.LoadCredentialsAPI
import com.google.spanner.util.TableDataBuilder
import com.google.cloud.spanner.Mutation
import com.google.cloud.spanner.ReadContext
import com.google.cloud.spanner.ResultSet
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Service
@Slf4j
class TableDataService {

	@Autowired
	LoadCredentialsAPI loadCredentialsAPI

	private Spanner spanner

	DatabaseClient getDatabaseClient(String url, String instance, String database) {
		InputStream ins = loadCredentialsAPI.getInputStreamURL(url)
		def json = new JsonSlurper().parse(ins)
		DatabaseClient dbClient
		try {
			String project = json?.project_id
			log.error("Project Id : {}",project)
			if(project && instance && database) {
				spanner = loadCredentialsAPI.getSpanner(url)
				DatabaseId db = DatabaseId.of(project, instance, database)
				dbClient = spanner.getDatabaseClient(db)
			}
		} catch(Exception e) {
			throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), e?.detailedMessage ?: e?.message, new Exception())
		}
		return dbClient
	}

	/** unprotected blind write. */
	String writeAtLeastOnce(String url,String instanceId, String databaseId,String table, List<Map> insertDataList) {
		DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
		try {
			def mutations = new ArrayList<>()			
			TableDataBuilder tdBuilder = new TableDataBuilder()
			mutations = tdBuilder.createMutationist(table, insertDataList)

			if(mutations && mutations.size() > 0) {
				Timestamp tm = dbClient.write(mutations)
				return "Succesfully Inserted data"
			} else {
				return "Something went wrong while inserting data"
			}
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner?.close()
		}
	}

	int insertorUpdateUsingDml(String url,String instanceId, String databaseId, String query) {
		DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
		long rowCount = -1
		try {
			dbClient.readWriteTransaction().run(
					new TransactionCallable<Void>() {
						@Override
						public Void run(TransactionContext transaction) throws Exception {
							rowCount = transaction.executeUpdate(Statement.of(query));
							log.info("Record inserted {}", rowCount)
							return null
						}
					});
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner?.close()
		}
		return rowCount.intValue()
	}



	/** single use with timestamp bound. */
	public String singleUseStale(String url,String instanceId, String databaseId, String table, long singerId) {
		DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
		String firstName
		try {			
			String column = "FirstName"
			Struct row = dbClient.singleUse(TimestampBound.ofMaxStaleness(10, TimeUnit.SECONDS))
					.readRow(table, Key.of(singerId), Collections.singleton(column))
			firstName = row.getString(column)
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
		} finally {
			spanner?.close()
		}
		return firstName
	}

	/** 
	 * Single use with timestamp bound. 
	 */
	List<Map> selectSemiQuery(String url,String instanceId, String databaseId, String table, String whereCondition, Set<String> fields) {
		DatabaseClient dbClient
		List<Map> finalList = new ArrayList()
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)

			Joiner joiner = Joiner.on(',')
			Statement query = Statement.newBuilder("SELECT ")
					.append(joiner.join(fields))
					.append(" FROM ")
					.append(table)
					.append(whereCondition ? "where "+ whereCondition : "")
					.build()
			ResultSet resultSet = dbClient.singleUse(TimestampBound.ofMaxStaleness(1, TimeUnit.MINUTES)).executeQuery(query)
			while (resultSet.next()) {
				def outputMap = [:]
				for(String field : fields) {
					outputMap.put(field, getObjectFromQuery(resultSet, 0, field))
				}
				finalList.add(outputMap)
			}
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner?.close()
		}
		return finalList
	}


	// [START spanner_delete_data]
	boolean deleteData(String url, String instanceId, String databaseId, String tableName, List<String> pKeyList) {
		DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
		boolean isSuccess
		try {
			def mutations = new ArrayList<>()
			if(pKeyList && pKeyList.size() > 0) {

				for (String pKey in pKeyList) {
					mutations.add(Mutation.delete(tableName, KeySet.singleKey(Key.newBuilder().append(pKey).build())))
				}

				dbClient.write(mutations)
				isSuccess = true
				log.info("Records deleted.\n")
			}

		} catch(e) {
			isSuccess = false
			log.error("Unexpected Error : {}",e.message)
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
				for (int i = 0; i < count; i++)
				{
					outputMap.put(i+1, getObjectFromQuery(resultSet, i, null))
				}
				finalList.add(outputMap)

			}
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner?.close()
		}
		return finalList
	}

	Object getObjectFromQuery(ResultSet resultSet, int i, String columnName) {
		Type type = resultSet.getColumnType(columnName ?: i)
		Object returnObject
		switch(type) {
			case Type.TYPE_INT64:
				returnObject = resultSet.getLong(columnName ?: i)
				break;
			case Type.TYPE_FLOAT64:
				returnObject = resultSet.getLong(columnName ?: i)
				break;
			case Type.TYPE_STRING:
				returnObject = resultSet.getString(columnName ?: i)
				break;
			case Type.TYPE_TIMESTAMP:
				returnObject = resultSet.getTimestamp(columnName ?: i)
				break;
			case Type.TYPE_DATE:
				returnObject = resultSet.getDate(columnName ?: i)
				break;
			case Type.TYPE_BYTES:
				returnObject = resultSet.getBytes(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_BOOL:
				returnObject = resultSet.getBooleanArray(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_BYTES:
				returnObject = resultSet.getBytesList(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_DATE:
				returnObject = resultSet.getDateList(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_TIMESTAMP:
				returnObject = resultSet.getTimestampList(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_FLOAT64:
				returnObject = resultSet.getLongArray(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_INT64:
				returnObject = resultSet.getDoubleList(columnName ?: i)
				break;
			case Type.TYPE_ARRAY_STRING:
				returnObject = resultSet.getStringList(columnName ?: i)
				break;

			default:
				returnObject = null
				break;
		}
		return returnObject
	}

	/**
	 *  ALl Delete Related Query
	 */

	// [START spanner_delete_data]
	boolean truncateTable(String url, String instanceId, String databaseId, String tableName) {
		DatabaseClient dbClient = getDatabaseClient(url, instanceId, databaseId)
		boolean isSuccess
		try {

			def mutations = new ArrayList<>()
			if (tableName) {
				mutations.add(Mutation.delete(tableName, KeySet.all()))
				dbClient.write(mutations)
				isSuccess = true
				log.info("Records deleted.\n")
			}
		} catch(e) {
			isSuccess = false
			log.error("Unexpected Error : {}",e.message)
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
						public Void run(TransactionContext transaction) throws Exception {
							//String sql = "DELETE FROM Singers WHERE FirstName = 'Alice'"
							rowCount = transaction.executeUpdate(Statement.of(query))
							log.info("Record deleted : {}", rowCount)
							return null
						}
					})
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner?.close()
		}
		return rowCount
	}
}

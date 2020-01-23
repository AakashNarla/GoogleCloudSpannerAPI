package com.google.spanner.service

import com.google.api.gax.longrunning.OperationFuture
import com.google.api.gax.paging.Page
import com.google.cloud.spanner.*
import com.google.common.collect.Iterables
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlMetadata
import com.google.spanner.util.LoadCredentialsAPI
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Service
@Slf4j
class DatabaseAdminService {

    @Autowired
    LoadCredentialsAPI loadCredentialsAPI

    private Spanner spanner

    private DatabaseAdminClient getDatabaseAdminClient(String url) {
        try {
            spanner = loadCredentialsAPI.getSpanner(url)
        } catch (Exception e) {
            throw e
        }
        return spanner?.getDatabaseAdminClient()
    }

    /** Example to create database. */
    Database createDatabase(String url, String instanceId, String databaseId) {

        OperationFuture<Database, CreateDatabaseMetadata> op =
                getDatabaseAdminClient(url).createDatabase(instanceId, databaseId, [])
        Database db = null
        try {
            db = op.get(1, TimeUnit.MINUTES)
        } catch (ExecutionException e) {
            throw (SpannerException) e.getCause()
        } catch (InterruptedException e) {
            throw SpannerExceptionFactory.propagateInterrupt(e)
        } finally {
            spanner?.close()
        }

        return db
    }


    /** getDatabase. */
    Database getDatabase(String url, String instanceId, String databaseId) {
        Database db = null
        try {
            db = getDatabaseAdminClient(url).getDatabase(instanceId, databaseId)
        } catch (Exception e) {
            throw e
        } finally {
            spanner?.close()
        }
        return db
    }

    /** Alter the table database DDL. */
    // Example Value : 'ALTER TABLE Albums ADD COLUMN MarkingBudget INT64'
    String updateTable(String url, String instanceId, String databaseId, String query) {
        String db = null
        try {
            OperationFuture<Void, UpdateDatabaseDdlMetadata> op = getDatabaseAdminClient(url)
                    .updateDatabaseDdl(
                            instanceId,
                            databaseId,
                            Arrays.asList(query),
                            null)
            op.get(1, TimeUnit.MINUTES)
            db = 'Successfully updated'
        } catch (ExecutionException e) {
            log.error('Unexpected Error : {}', e.message)
            throw (SpannerException) e.getCause()
        } catch (InterruptedException e) {
            log.error('Unexpected Error : {}', e.message)
            throw SpannerExceptionFactory.propagateInterrupt(e)
        } catch (Exception e) {
            log.error('Unexpected Error : {}', e.message)
            throw e
        } finally {
            spanner?.close()
        }
        return db
    }

    /** Drop a Cloud Spanner database. */
    String dropDatabase(String url, String instanceId, String databaseId) {
        String dropDatabase = null
        if (instanceId && databaseId) {
            try {
                getDatabaseAdminClient(url).dropDatabase(instanceId, databaseId)
                dropDatabase = 'Successfully Deleted'
            } catch (Exception e) {
                throw e
            } finally {
                spanner?.close()
            }
        }
        return dropDatabase
    }

    /** Example to get the schema of a Cloud Spanner database. */
    List<String> getDatabaseDdl(String url, String instanceId, String databaseId) {
        def statementsInDb = null
        try {
            statementsInDb = getDatabaseAdminClient(url).getDatabaseDdl(instanceId, databaseId)
        } catch (Exception e) {
            throw e
        } finally {
            spanner?.close()
        }
        return statementsInDb
    }

    /**Get the list of Cloud Spanner database in the given instance. */
    List<Database> listDatabases(String url, String instanceId) {
        def dbs = new ArrayList<Database>()
        try {
            Page<Database> page = getDatabaseAdminClient(url).listDatabases(instanceId, Options.pageSize(1))
            //dbs = new ArrayList<>()
            while (page != null && page.getValues().size() > 0) {
                Database db = Iterables.getOnlyElement(page.getValues())
                dbs.add(db)
                page = page.getNextPage()
            }
        } catch (Exception e) {
            throw e
        } finally {
            spanner?.close()
        }
        return dbs
    }
}

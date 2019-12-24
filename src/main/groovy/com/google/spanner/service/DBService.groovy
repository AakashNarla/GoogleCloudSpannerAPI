package com.google.spanner.service

import java.util.concurrent.TimeUnit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import com.google.auth.oauth2.GoogleCredentials
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
import com.google.spanner.exception.ResourceNotFoundException
import com.google.spanner.util.LoadCredentialsAPI
import com.google.cloud.spanner.Mutation
import com.google.cloud.spanner.ResultSet
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Service
@Slf4j
class DBService {

	@Autowired
	LoadCredentialsAPI loadCredentialsAPI

	private Spanner spanner

	DatabaseClient getDatabaseClient(String url, String instance, String database) {
		InputStream ins = loadCredentialsAPI.getInputStreamURL(url)
		def json = new JsonSlurper().parse(ins)
		DatabaseClient dbClient
		try {
			String project = json?.project_id

			if(project & instance && database) {
				spanner = loadCredentialsAPI.getSpanner(url)
				DatabaseId db = DatabaseId.of(project, instance, database)
				dbClient = spanner.getDatabaseClient(db)
			}
		} catch(Exception e) {
			throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), e?.message, e)
		}
		return dbClient
	}

	/** Example of unprotected blind write. */
	public void writeAtLeastOnce(String url,String instanceId, String databaseId,long singerId) {
		DatabaseClient dbClient
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
			Mutation mutation =
					Mutation.newInsertBuilder("Singers")
					.set("SingerId")
					.to(singerId)
					.set("FirstName")
					.to("Billy")
					.set("LastName")
					.to("Joel")
					.build()

			dbClient.writeAtLeastOnce(Collections.singletonList(mutation))
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner.close()
		}
	}

	/** Example of single use with timestamp bound. */
	public String singleUseStale(String url,String instanceId, String databaseId, long singerId) {
		DatabaseClient dbClient
		String firstName
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
			String column = "FirstName"
			Struct row = dbClient.singleUse(TimestampBound.ofMaxStaleness(10, TimeUnit.SECONDS))
					.readRow("Singers", Key.of(singerId), Collections.singleton(column))
			firstName = row.getString(column)
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
		} finally {
			spanner.close()
		}
		return firstName
	}

	/** Example of single use with timestamp bound. */
	String singleUse(String url,String instanceId, String databaseId, long singerId) {
		DatabaseClient dbClient
		String firstName
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
			String column = "FirstName"
			Struct row = dbClient.singleUse().readRow("Singers", Key.of(singerId), Collections.singleton(column))
			firstName = row.getString(column)
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner.close()
		}
		return firstName
	}

	// [START spanner_delete_data]
	boolean deleteData(String url, String instanceId, String databaseId, String tableName, List<String> pKeyList) {
		DatabaseClient dbClient
		boolean isSuccess
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
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
			spanner.close()
		}
		return isSuccess
	}

	// [START spanner_delete_data]
	boolean truncateTable(String url, String instanceId, String databaseId, String tableName) {
		DatabaseClient dbClient
		boolean isSuccess
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
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
			spanner.close()
		}
		return isSuccess
	}

	// [START spanner_dml_standard_delete]
	int deleteUsingDml(String url, String instanceId, String databaseId, String query) {
		DatabaseClient dbClient
		long rowCount = -1
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
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
			spanner.close()
		}
		return rowCount
	}

	// [START spanner_query_data]
	void query(String url, String instanceId, String databaseId, String query) {
		DatabaseClient dbClient
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
			ResultSet resultSet = dbClient
					.singleUse() // Execute a single read or query against Cloud Spanner.
					.executeQuery(Statement.of("SELECT SingerId, AlbumId, AlbumTitle FROM Albums"))
			while (resultSet.next()) {
				System.out.printf(
						"%d %d %s\n", resultSet.getLong(0), resultSet.getLong(1), resultSet.getString(2))
			}
		} catch(e) {
			log.error("Unexpected Error : {}",e.message)
			throw e
		} finally {
			spanner.close()
		}
	}
}

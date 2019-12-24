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
import com.google.cloud.spanner.Mutation;
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
		} finally {
			spanner.close()
		}
		return firstName
	}

	/** Example of single use with timestamp bound. */
	public String singleUse(String url,String instanceId, String databaseId, long singerId) {
		DatabaseClient dbClient
		String firstName
		try {
			dbClient = getDatabaseClient(url, instanceId, databaseId)
			String column = "FirstName"
			Struct row = dbClient.singleUse().readRow("Singers", Key.of(singerId), Collections.singleton(column))
			firstName = row.getString(column)
		} catch(e) {
		} finally {
			spanner.close()
		}
		return firstName
	}

	// [START spanner_delete_data]
	static void deleteExampleData(DatabaseClient dbClient) {
		List<Mutation> mutations = new ArrayList<>();
		// KeySet.singleKey() can be used to delete one row at a time.
		mutations.add(
				Mutation.delete(
				"Singers", KeySet.singleKey(Key.newBuilder().append('singerId').build())))

		dbClient.write(mutations);
		System.out.printf("Records deleted.\n");
	}
	
	// [START spanner_delete_data]
	static void deleteTable(DatabaseClient dbClient) {
		List<Mutation> mutations = new ArrayList<>();

		// KeySet.all() can be used to delete all the rows in a table.
		mutations.add(Mutation.delete("Albums", KeySet.all()));

		
		dbClient.write(mutations);
		System.out.printf("Records deleted.\n");
	}
	// [END spanner_delete_data]
	// [START spanner_dml_standard_delete]
	static void deleteUsingDml(DatabaseClient dbClient) {
	  dbClient
		  .readWriteTransaction()
		  .run(
			  new TransactionCallable<Void>() {
				@Override
				public Void run(TransactionContext transaction) throws Exception {
				  String sql = "DELETE FROM Singers WHERE FirstName = 'Alice'";
				  long rowCount = transaction.executeUpdate(Statement.of(sql));
				  System.out.printf("%d record deleted.\n", rowCount);
				  return null;
				}
			  });
	}
	// [START spanner_query_data]
	static void query(DatabaseClient dbClient) {
		try {
			ResultSet resultSet = dbClient
					.singleUse() // Execute a single read or query against Cloud Spanner.
					.executeQuery(Statement.of("SELECT SingerId, AlbumId, AlbumTitle FROM Albums"))
			while (resultSet.next()) {
				System.out.printf(
						"%d %d %s\n", resultSet.getLong(0), resultSet.getLong(1), resultSet.getString(2));
			}
		} catch(e) {

		}
	}
}

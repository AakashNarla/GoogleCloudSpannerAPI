package com.google.spanner.service

import com.google.api.gax.longrunning.OperationFuture
import com.google.api.gax.paging.Page
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.Database
import com.google.cloud.spanner.DatabaseAdminClient
import com.google.cloud.spanner.Options
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerException
import com.google.cloud.spanner.SpannerExceptionFactory
import com.google.cloud.spanner.SpannerOptions
import com.google.common.collect.Iterables
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlMetadata
import com.google.spanner.util.LoadCredentialsAPI

import groovy.util.logging.Slf4j

import java.util.ArrayList
import java.util.Arrays
import java.util.List
import java.util.concurrent.ExecutionException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
public class DatabaseAdminService {

	@Autowired
	LoadCredentialsAPI loadCredentialsAPI

	private Spanner spanner

	private DatabaseAdminClient getDatabaseAdminClient(String url) {
		try {
			spanner = loadCredentialsAPI.getSpanner(url)
		}catch (Exception e) {
			throw e
		}
		return spanner?.getDatabaseAdminClient()
	}

	/** Example to create database. */
	public Database createDatabase(String url, String instanceId, String databaseId) {

		OperationFuture<Database, CreateDatabaseMetadata> op =
				getDatabaseAdminClient(url).createDatabase(instanceId, databaseId, [])
		Database db
		try {
			db = op.get()
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
	public Database getDatabase(String url,String instanceId, String databaseId) {
		Database db
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
	// Example Value : "ALTER TABLE Albums ADD COLUMN MarkingBdget INT64"
	public Database updateTable(String url,String instanceId, String databaseId, String query) {
		Database db
		try {
			OperationFuture<Void, UpdateDatabaseDdlMetadata> op = getDatabaseAdminClient(url)
					.updateDatabaseDdl(
					instanceId,
					databaseId,
					Arrays.asList(query),
					null)
			log.info(op.get())
			db= op.get()
		} catch (ExecutionException e) {
			throw (SpannerException) e.getCause()
		} catch (InterruptedException e) {
			throw SpannerExceptionFactory.propagateInterrupt(e)
		} finally {
			spanner?.close()
		}
		return db
	}

	/** Drop a Cloud Spanner database. */
	public String dropDatabase(String url,String instanceId, String databaseId) {
		if(instanceId && databaseId) {
			try {
				getDatabaseAdminClient(url).dropDatabase(instanceId, databaseId)
				return "Succesfully Deleted"
			} catch (Exception e) {
				throw e
			} finally {
				spanner?.close()
			}
		}
	}

	/** Example to get the schema of a Cloud Spanner database. */
	public List<String> getDatabaseDdl(String url,String instanceId, String databaseId) {
		def statementsInDb
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
	public List<Database> listDatabases(String url, String instanceId) {
		def dbs
		try {
			Page<Database> page = getDatabaseAdminClient(url).listDatabases(instanceId, Options.pageSize(1))
			dbs = new ArrayList<>()
			while (page != null && page.getValues().size() > 0) {
				Database db = Iterables.getOnlyElement(page.getValues())
				dbs.add(db)
				page = page.getNextPage()
			}
		}catch (Exception e) {
			throw e
		} finally {
			spanner?.close()
		}
		return dbs
	}
}

package com.google.spanner.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.DatabaseAdminClient
import com.google.cloud.spanner.DatabaseClient
import com.google.cloud.spanner.DatabaseId
import com.google.cloud.spanner.InstanceAdminClient
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions

import groovy.util.logging.Slf4j

@Component
@Slf4j
class LoadCredentialsAPI {
	@Value(value = "classpath:helical-fin.json")
	private Resource companiesXml

	public InputStream getInputStreamURL(String url) {
		InputStream inputStream
		try {
			URL urlObject = new URL(url);
			URLConnection urlConnection = urlObject.openConnection()
			inputStream = urlConnection.getInputStream()
		} catch(Exception e) {
			log.error("Exception Loading the file {} ", e.getMessage())
			inputStream = null
		}
		return inputStream
	}

	GoogleCredentials createGoogleCredentials(String url) {
		InputStream inp = getInputStreamURL(url)
		inp = inp ?:  companiesXml.getInputStream()
		return GoogleCredentials.fromStream(inp)
	}

	InstanceAdminClient getInstanceAdminClientCred(String url) {
		InstanceAdminClient instanceAdminClient
		try {
			GoogleCredentials credentials = createGoogleCredentials(url)
			Spanner spanner  = SpannerOptions.newBuilder().setCredentials(credentials).build()?.getService()
			instanceAdminClient = spanner.getInstanceAdminClient()
		} catch(Exception e) {
		}
		return instanceAdminClient
	}

	DatabaseAdminClient getDatabaseAdminClient(String url) {
		DatabaseAdminClient dbAdminClient
		try {
			GoogleCredentials credentials = createGoogleCredentials(url)
			Spanner spanner  = SpannerOptions.newBuilder().setCredentials(credentials).build()?.getService()
			dbAdminClient = spanner.getDatabaseAdminClient()
		} catch(Exception e) {
		}

		return dbAdminClient
	}

	DatabaseClient getDatabaseClient(String url, String project, String instance, String database) {
		DatabaseClient dbClient
		try {
			if(project && instance && database) {
				GoogleCredentials credentials = createGoogleCredentials(url)
				Spanner spanner  = SpannerOptions.newBuilder().setCredentials(credentials).build()?.getService()
				DatabaseId db = DatabaseId.of(project, instance, database)
				dbClient = spanner.getDatabaseClient(db)
			}
		} catch(Exception e) {
		}
		return dbClient
	}
}
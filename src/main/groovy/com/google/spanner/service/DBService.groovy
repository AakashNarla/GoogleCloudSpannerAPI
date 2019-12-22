package com.google.spanner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.DatabaseClient
import com.google.cloud.spanner.DatabaseId
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

@Service
@Slf4j
class DBService {

	
	String baseUrl = "https://spanner.googleapis.com/v1/projects/{projectId}/instances/{instanceId}"

	String accessTokenUrl = "?access_token={access_token}"
	DatabaseClient getDatabaseClient() {
		// [START get_db_client]
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
		Spanner spanner  = SpannerOptions.newBuilder().setCredentials(credentials).build().service
		final String project = "test-project";
		final String instance = "test-instance";
		final String database = "example-db";
		DatabaseId db = DatabaseId.of(project, instance, database);
		DatabaseClient dbClient = spanner.getDatabaseClient(db);
		// [END get_db_client]

		return dbClient;
	}
	
}

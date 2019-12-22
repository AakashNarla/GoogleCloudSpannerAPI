package com.google.spanner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.DatabaseClient
import com.google.cloud.spanner.DatabaseId
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions
import com.google.spanner.util.GoogleAPIConnector

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

@Service
@Slf4j
class DBService {

	@Autowired
	GoogleAPIConnector googleAPIConnector

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
	public String getDatatbaseList(String accessToken, String projectId, String instanceId) {
		Map pathVaraible  = createPathVariable(accessToken, projectId, instanceId)
		String dbUrl =  baseUrl + "/databases"+ accessTokenUrl

		def responseStr = googleAPIConnector.getResponseFromAPIs(dbUrl, pathVaraible)

		if(StringUtils.isEmpty(responseStr)) {
			responseStr = ""
		}

		return responseStr
	}

	public String getDatatbaseState(String accessToken, String projectId, String instanceId, String database) {
		Map pathVaraible  = createPathVariable(accessToken, projectId, instanceId)
		pathVaraible.put("database", database)
		String dbUrl =  baseUrl + "/databases/{database}"+ accessTokenUrl

		def responseStr = googleAPIConnector.getResponseFromAPIs(dbUrl, pathVaraible)

		if(StringUtils.isEmpty(responseStr)) {
			responseStr = ""
		}

		return responseStr
	}

	public String createDatabase(String accessToken, String projectId, String instanceId, String database) {
		Map pathVaraible  = createPathVariable(accessToken, projectId, instanceId)

		String dbUrl =  baseUrl + "/databases"+ accessTokenUrl
		def bodyMap = ["createStatement":"create DATABASE `${database}`"]

		String jsonBody = JsonOutput.toJson(bodyMap)

		def responseStr = googleAPIConnector.postResponseToAPIs(dbUrl, jsonBody, pathVaraible)

		if(StringUtils.isEmpty(responseStr)) {
			responseStr = ""
		}

		return responseStr
	}

	private Map createPathVariable(String accessToken,String projectId, String instanceId) {
		Map<String, String> pathVaraible = new HashMap()
		if(accessToken?.trim() && projectId?.trim() && instanceId?.trim()) {
			pathVaraible.put("projectId", projectId.trim())
			pathVaraible.put("instanceId", instanceId.trim())
			pathVaraible.put("access_token", accessToken.trim())
		}
		return pathVaraible
	}
}

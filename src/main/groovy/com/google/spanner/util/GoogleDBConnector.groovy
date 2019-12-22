package com.google.spanner.util

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.DatabaseAdminClientImpl
import com.google.cloud.spanner.DatabaseClient
import com.google.cloud.spanner.DatabaseId
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions
import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient
import com.google.spanner.admin.database.v1.Database
import groovy.util.logging.Slf4j

@Component
@Slf4j
class GoogleDBConnector {

	String baseUrl = "https://spanner.googleapis.com/v1/projects/{projectId}/instances/{instanceId}"
	
	String accessTokenUrl = "?access_token={access_token}"
	
	RestTemplate restTemplate = new RestTemplate()

	public String getResponseFromAPIs(String jsonPath, SpannerOptions options, HashMap<String, String> pathVariables) {
		ResponseEntity<String> responseEntity

		if(restTemplate == null) {
			restTemplate = new RestTemplate()
		}
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
		Spanner spanner = SpannerOptions.newBuilder().setCredentials(credentials).build().getService()
		//Spanner spanner = options.getService()
		String command = args[0];
		DatabaseId dbId = DatabaseId.of(options.getProjectId(), instance, database);
		DatabaseClient dbClient = spanner.getDatabaseClient(dbId)
		
		DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient()
		//DatabaseAdminClientImpl dbAdminClient = new DatabaseAdminClientImpl(options.getProjectId(), );
		Database db = dbAdminClient.getDatabase(instance, databaseId)
		String response = null
		try {
			if(pathVariables != null) {
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String , pathVariables)
			} else {
				responseEntity = restTemplate.exchange(url, String)
			}

			response = responseEntity.getBody()
		} catch (Exception e) {
			log.error("Unable to make a rest call to ${url}"+ e.message, e)
			throw e
		}
		return response
	}

	public String postResponseToAPIs(String url, String body, HashMap<String, String> pathVariables) {
		ResponseEntity<String> responseEntity

		if(restTemplate == null) {
			restTemplate = new RestTemplate()
		}
		String response = null

		HttpHeaders headers = new HttpHeaders()
		headers.setContentType(MediaType.APPLICATION_JSON)

		HttpEntity<String> entity = new HttpEntity<String>(body,headers)
		try {
			if(pathVariables != null && body != null) {
				responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String , pathVariables)
			} else {
				responseEntity = restTemplate.exchange(url, String)
			}

			response = responseEntity.getBody()
		} catch (Exception e) {
			log.error("Unable to make a rest call to ${url}"+ e.message, e)
			throw e
		}
		return response
	}
}

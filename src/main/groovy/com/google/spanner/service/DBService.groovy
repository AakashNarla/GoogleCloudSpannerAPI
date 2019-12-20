package com.google.spanner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

import com.google.spanner.util.GoogleAPIConnector

import groovy.json.JsonOutput

@Service
class DBService {

	@Autowired
	GoogleAPIConnector googleAPIConnector

	String baseUrl = "https://spanner.googleapis.com/v1/projects/{projectId}/instances/{instanceId}"

	String accessTokenUrl = "?access_token={access_token}"

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

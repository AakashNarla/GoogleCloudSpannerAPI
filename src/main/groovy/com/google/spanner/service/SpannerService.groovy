package com.google.spanner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

import com.google.spanner.util.RestTemplateHelper

@Service
class SpannerService {

	@Autowired
	RestTemplateHelper restTemplateHelper

	String baseUrl = "https://spanner.googleapis.com/v1/projects/{projectId}/instances/{instanceId}"

	String accessTokenUrl = "?access_token={access_token}"

	public String getDatatbaseList(String accessToken, String projectId, String instanceId) {
		Map<String, String> pathVaraible = new HashMap()
		pathVaraible.put("projectId", projectId)
		pathVaraible.put("instanceId", instanceId)
		pathVaraible.put("access_token", accessToken)
		String dbUrl =  baseUrl + "/databases"+ accessTokenUrl

		def responseStr = restTemplateHelper.getResponseFromAPIs(dbUrl, pathVaraible)

		if(StringUtils.isEmpty(responseStr)) {
			responseStr = ""
		}

		return responseStr
	}

	public String getInstanceState(String accessToken,String projectId, String instanceId) {
		Map<String, String> pathVaraible = new HashMap()
		pathVaraible.put("projectId", projectId)
		pathVaraible.put("instanceId", instanceId)
		pathVaraible.put("access_token", accessToken)

		def stateUrl =  baseUrl + accessTokenUrl
		String responseStr = restTemplateHelper.getResponseFromAPIs(stateUrl, pathVaraible)

		if(StringUtils.isEmpty(responseStr)) {
			responseStr = ""
		}

		return responseStr
	}
}

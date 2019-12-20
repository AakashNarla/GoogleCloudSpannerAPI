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

import groovy.util.logging.Slf4j

@Component
@Slf4j
class RestTemplateHelper {


	RestTemplate restTemplate = new RestTemplate()

	public String getResponseFromAPIs(String url, HashMap<String, String> pathVariables) {
		ResponseEntity<String> responseEntity

		if(restTemplate == null) {
			restTemplate = new RestTemplate()
		}
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

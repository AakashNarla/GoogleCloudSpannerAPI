package com.google.spanner.util

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpMethod
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
	

}

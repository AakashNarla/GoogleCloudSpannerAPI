package com.google.spanner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.annotations.*;

import com.google.cloud.spanner.Instance
import com.google.cloud.spanner.InstanceConfig
import com.google.spanner.object.ResponseWrapper
import com.google.spanner.service.InstanceAdminService

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api

@RestController
@RequestMapping("/v1/spanner/instance/")
@Api(tags = ["Instance"], description = "Spanner Instance API")
@Slf4j
class InstanceAdminController {

	@Autowired
	InstanceAdminService spannerService

	@ApiOperation(value = "Return All Spanner Instance Config Data")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Get All Instance Config"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/configs")
	ResponseEntity<String> getAllInstanceConfigs(@RequestParam(name = "url", required = true)String url){
		List<InstanceConfig> result = spannerService.listInstanceConfigs(url)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Return All Spanner Instance")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Get All Instance Config"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/")
	ResponseEntity<String> getAllInstance(@RequestParam(name = "url", required = true)String url){
		List<Instance> result = spannerService.listInstances(url)
		return ResponseEntity.ok().body(result)
	}


	@ApiOperation(value = "Create new Spanner instance")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully created a new instance"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/create")
	ResponseEntity<String> createInstance(
			@RequestParam(name = "url", required = true)String url,
			@RequestParam(name = "instance-id", required = true)String instanceId,
			@RequestParam(name = "config-id", required = true)String configId,
			@RequestParam(name = "node-count", required = true)int nodeCount ){
		String result = spannerService.createInstance(url, instanceId, configId, nodeCount)
		return ResponseEntity.ok().body(new ResponseWrapper(result: result))
	}

	@ApiOperation(value = "Update an existing Spanner instance display name and node count")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully updated instance"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/update")
	ResponseEntity<String> updateInstance (
			@RequestParam(name = "url", required = true)String url,
			@RequestParam(name = "instance-id", required = true)String instanceId,
			@RequestParam(name = "newDisplayName", required = true)String newDisplayName,
			@RequestParam(name = "node-count", required = true)int nodeCount ){
		String result = spannerService.updateInstance(url, instanceId, newDisplayName, nodeCount)
		return ResponseEntity.ok().body(new ResponseWrapper(result: result))
	}

	@ApiOperation(value = "Return Spanner instance state")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully instance state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{instance-id}")
	ResponseEntity<String> getInstanceState(@PathVariable(name = "instance-id", required = true)String instanceId,
			@RequestParam(name = "url", required = true)String url){
		String result = spannerService.getInstance(url, instanceId)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Return Spanner instance Config")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully instance state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{instance-id}/config")
	ResponseEntity<String> getInstanceConfig(@PathVariable(name = "instance-id", required = true)String instanceId,
			@RequestParam(name = "url", required = true)String url){
		String result = spannerService.getInstanceConfig(url, instanceId)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Delete Spanner instance")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Deleted"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@DeleteMapping("/{instance-id}/delete")
	ResponseEntity<String> deleteInstance(
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@RequestParam(name = "url", required = true)String url){
		String result = spannerService.deleteInstance(url, instanceId)
		return ResponseEntity.ok().body(new ResponseWrapper(result: result))
	}
}

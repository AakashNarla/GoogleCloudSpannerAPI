package com.google.spanner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.annotations.*;

import com.google.spanner.service.DBService
import com.google.spanner.service.InstanceService
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api

@RestController
@RequestMapping("/v1/spanner/{project-id}/{instance-id}/databases")
@Api(tags = ["Database"], description = "Spanner Database API")
@Slf4j
class DBController {

	@Autowired
	DBService dbService

	@ApiOperation(value = "Returns list of all databases")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully retrieved list"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/")
	String getDatabases(
		@RequestParam(name = "access_token", required = true)String accessToken,
		@PathVariable(name = "project-id", required = true)String projectId,
		@PathVariable(name = "instance-id", required = true)String instanceId){
		String result = dbService.getDatatbaseList(accessToken, projectId, instanceId)
		return result
	}

	
	@ApiOperation(value = "Create a Database")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Create a database"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/create/{database-name}")
	String createDatabase(
			@RequestParam(name = "access_token", required = true)String accessToken,
			@PathVariable(name = "project-id", required = true)String projectId,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database-name", required = true)String database){
		String result = dbService.createDatabase(accessToken, projectId, instanceId, database)
		return result
	}
	
	@ApiOperation(value = "Returns state of a database")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{database}")
	String getDatabaseState(
			@RequestParam(name = "access_token", required = true)String accessToken,
			@PathVariable(name = "project-id", required = true)String projectId,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database){
		String result = dbService.getDatatbaseState(accessToken, projectId, instanceId, database)
		return result
	}
}

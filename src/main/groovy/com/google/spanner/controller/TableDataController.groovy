package com.google.spanner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.annotations.*;

import com.google.spanner.service.DBService
import com.google.spanner.service.DatabaseAdminService
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api

@RestController
@RequestMapping("/v1/spanner/{instance-id}/{database}")
@Api(tags = ["Table"], description = "Spanner Table Data API")
@Slf4j
class TableDataController {

	@Autowired
	DBService dbService

	@ApiOperation(value = "Delete the records as per ids")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully retrieved list"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{table}/delete")
	ResponseEntity<?> deleteTable(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table){
		def result = dbService.deleteData(url, instanceId, database, table, pKeyList)
		return ResponseEntity.ok().body(result)
	}


	@ApiOperation(value = "Trucate a Table")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Create a database"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{table}/delete/all")
	ResponseEntity<?>  truncateTable(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table){
		def result = dbService.truncateTable(url, instanceId, database, table)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Delete the data as per query")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully deleted reocords count"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/delete/query")
	ResponseEntity<?>  dropDatabase(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@RequestParam(name = "query", required = true)String query){
		def result = dbService.deleteUsingDml(url, instanceId, database, query)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Returns state of a database")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{table}/get/query")
	ResponseEntity<?>  getDatabaseState(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table,
			@RequestParam(name = "query", required = true)String query){
		def result = dbService.query(url, instanceId, database)
		return ResponseEntity.ok().body(result)
	}
}

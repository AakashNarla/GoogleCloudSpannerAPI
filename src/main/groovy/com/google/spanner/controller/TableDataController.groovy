package com.google.spanner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.annotations.*;

import com.google.spanner.service.TableDataService
import com.google.spanner.service.DatabaseAdminService
import groovy.util.logging.Slf4j

@RestController
@RequestMapping("/v1/spanner/{instance-id}/{database}")
@Api(tags = ["Table"], description = "Spanner Table Data API")
@Slf4j
class TableDataController {

	@Autowired
	TableDataService tableDataService

	@Autowired
	DatabaseAdminService dbAdminService

	@ApiOperation(value = "Create/Alter/Drop a table in a database")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Create/Alter/Drop table"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/table")
	ResponseEntity<?> alterDatabaseTable(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@ApiParam(name = "query",value="'CREATE TABLE Singers (SingerId   INT64 NOT NULL,  FirstName  STRING(1024),  LastName   STRING(1024),  SingerInfo BYTES(MAX)) PRIMARY KEY (SingerId)' OR 'ALTER TABLE Albums ADD COLUMN XXX INT64'")
			@RequestParam(name = "query", required = true)String query){
		def result = dbAdminService.updateTable(url, instanceId, database, query)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Delete the records as per ids")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully retrieved list"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@DeleteMapping("/{table}/delete")
	ResponseEntity<?> deleteTable(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table,
			@RequestParam(name = "primary-key", required = true)List<String> pKeyList){
		def result = tableDataService.deleteData(url, instanceId, database, table, pKeyList)
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
	@DeleteMapping("/{table}/delete/all")
	ResponseEntity<?>  truncateTable(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table){
		def result = tableDataService.truncateTable(url, instanceId, database, table)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Returns the Data with coloumn name")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{table}/get")
	ResponseEntity<?>  getTableData(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table,
			@ApiParam(name = "where-condition", value="'id > 1' or 'LIMIT 100'")
			@RequestParam(name = "where-condition", required = false )String whereCondition,
			@RequestParam(name = "columns", required = true)Set<String> columns){
		def result = tableDataService.selectSemiQuery(url, instanceId, database, table, whereCondition, columns)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Insert the Data")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/{table}/insert")
	ResponseEntity<?>  insertTableData(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@PathVariable(name = "table", required = true)String table,
			@RequestBody(required = true) List<Map<String, String>> requestBody){
		def result = tableDataService.writeAtLeastOnce(url, instanceId, database, table, requestBody)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Get data using Query")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/query/get")
	ResponseEntity<?>  getTableQuery(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@RequestParam(name = "query", required = true)String query){
		def result = tableDataService.query(url, instanceId, database, query)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Insert the Data using query")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/query/insert")
	ResponseEntity<?>  insertTableDataQuery(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@RequestParam(name = "query", required = true)String query){
		def result = tableDataService.insertorUpdateUsingDml(url, instanceId, database, query)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Update the Data using query")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully Retrieve database state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@PostMapping("/query/update")
	ResponseEntity<?>  updateTableDataUsingQuery(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@RequestParam(name = "query", required = true)String query){
		def result = tableDataService.insertorUpdateUsingDml(url, instanceId, database, query)
		return ResponseEntity.ok().body(result)
	}

	@ApiOperation(value = "Delete the data using query")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully deleted reocords count"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@DeleteMapping("/query/delete")
	ResponseEntity<?>  dropDatabase(
			@RequestParam(name = "url", required = true)String url,
			@PathVariable(name = "instance-id", required = true)String instanceId,
			@PathVariable(name = "database", required = true)String database,
			@RequestParam(name = "query", required = true)String query){
		def result = tableDataService.deleteUsingDml(url, instanceId, database, query)
		return ResponseEntity.ok().body(result)
	}
}

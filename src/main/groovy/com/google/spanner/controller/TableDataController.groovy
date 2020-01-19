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
import io.swagger.annotations.*

import com.google.spanner.service.TableDataService
import com.google.spanner.object.ResponseWrapper
import com.google.spanner.service.DatabaseAdminService
import groovy.util.logging.Slf4j

@RestController
@RequestMapping("/v1/spanner/{instance-id}/{database}")
@Api(tags = ["Table"], description = "Spanner Table Data API")
@Slf4j
class TableDataController {

    @Autowired
    TableDataService tableDataService

    @ApiOperation(value = "Delete the records as per ids")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully Delete the Data"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @DeleteMapping("/{table}/delete")
    ResponseEntity<?> deleteTable(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database", required = true) String database,
            @PathVariable(name = "table", required = true) String table,
            @RequestParam(name = "primary-key", required = true) List<String> pKeyList) {
        def result = tableDataService.deleteData(url, instanceId, database, table, pKeyList)
        return ResponseEntity.ok().body(new ResponseWrapper(message: 'Successfully Deleted Data', result: result))
    }


    @ApiOperation(value = "Truncate a Table")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully Truncate a database"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @DeleteMapping("/{table}/delete/all")
    ResponseEntity<?> truncateTable(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database", required = true) String database,
            @PathVariable(name = "table", required = true) String table) {
        def result = tableDataService.truncateTable(url, instanceId, database, table)
        return ResponseEntity.ok().body(new ResponseWrapper(message: 'Successfully Deleted Data', result: result))
    }

    @ApiOperation(value = "Returns the Data with column name")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully Retrieve database state"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @PostMapping("/{table}/get")
    ResponseEntity<?> getTableData(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database", required = true) String database,
            @PathVariable(name = "table", required = true) String table,
            @ApiParam(name = "where-condition", value = "'id > 1' or 'LIMIT 100'")
            @RequestParam(name = "where-condition", required = false) String whereCondition,
            @RequestParam(name = "columns", required = true) Set<String> columns) {
        def result = tableDataService.selectSemiQuery(url, instanceId, database, table, whereCondition, columns)
        return ResponseEntity.ok().body(result)
    }

    @ApiOperation(value = "Insert the Data")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully Insert the data into database"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @PostMapping(path = "/{table}/insert", consumes = "application/json", produces = "application/json")
    ResponseEntity<?> insertTableData(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database", required = true) String database,
            @PathVariable(name = "table", required = true) String table,
            @RequestBody(required = true) List<Map<String,String>> requestBody) {
        def result = tableDataService.writeAtLeastOnce(url, instanceId, database, table, requestBody)
        return ResponseEntity.ok().body(new ResponseWrapper(message: 'Successfully Inserted Data', result: result))
    }


}

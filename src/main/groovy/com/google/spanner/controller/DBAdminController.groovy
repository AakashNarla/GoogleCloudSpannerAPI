package com.google.spanner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.annotations.*

import com.google.spanner.service.TableDataService
import com.google.spanner.object.ResponseWrapper
import com.google.spanner.service.DatabaseAdminService
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api

@RestController
@RequestMapping("/v1/spanner/{instance-id}/databases")
@Api(tags = ["Database"], description = "Spanner Database Admin API")
@Slf4j
class DBAdminController {

    @Autowired
    DatabaseAdminService dbAdminService

    @ApiOperation(value = "Returns list of all databases")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @PostMapping("/")
    ResponseEntity<?> getDatabases(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId) {
        def result = dbAdminService.listDatabases(url, instanceId)
        return ResponseEntity.ok().body(result)
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
    ResponseEntity<?> createDatabase(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database-name", required = true) String database) {
        def result = dbAdminService.createDatabase(url, instanceId, database)
        return ResponseEntity.ok().body(result)
    }

    @ApiOperation(value = "Drop a Database")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully Create a database"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @DeleteMapping("/drop/{database-name}")
    ResponseEntity<?> dropDatabase(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database-name", required = true) String database) {
        def result = dbAdminService.dropDatabase(url, instanceId, database)
        return ResponseEntity.ok().body(new ResponseWrapper(result: result))
    }

    @ApiOperation(value = "Returns state of a database")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully Retrieve database state"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ]
    )
    @PostMapping("/{database}/status")
    ResponseEntity<?> getDatabaseState(
            @RequestParam(name = "url", required = true) String url,
            @PathVariable(name = "instance-id", required = true) String instanceId,
            @PathVariable(name = "database", required = true) String database) {
        def result = dbAdminService.getDatabase(url, instanceId, database)
        return ResponseEntity.ok().body(result)
    }
}

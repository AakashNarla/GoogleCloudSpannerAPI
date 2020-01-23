package com.google.spanner.controller

import com.google.spanner.object.ResponseWrapper
import com.google.spanner.service.DatabaseAdminService
import com.google.spanner.service.TableDataService
import groovy.util.logging.Slf4j
import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping('/v1/spanner/{instance-id}/{database}/query')
@Api(tags = ['Table Query'], description = 'Spanner Table Data Using Query API')
@Slf4j
class TableQueryDataController {

    @Autowired
    TableDataService tableDataService

    @Autowired
    DatabaseAdminService dbAdminService

    @ApiOperation(value = 'Create/Alter/Drop a table in a database')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Create/Alter/Drop table'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/table')
    ResponseEntity<?> alterDatabaseTable(
            @RequestParam(name = 'url', required = true) String url,
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @PathVariable(name = 'database', required = true) String database,
            @ApiParam(name = 'query', value = "CREATE TABLE Singers (SingerId   INT64 NOT NULL,  FirstName  STRING(1024),  LastName   STRING(1024),  SingerInfo BYTES(MAX)) PRIMARY KEY (SingerId)' OR 'ALTER TABLE Albums ADD COLUMN XXX INT64'")
            @RequestParam(name = 'query', required = true) String query) {
        def result = dbAdminService.updateTable(url, instanceId, database, query)
        return ResponseEntity.ok().body(new ResponseWrapper(message: result, status: 'success'))
    }

    @ApiOperation(value = 'Get data using Query')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Retrieve Data from Table'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/get')
    ResponseEntity<?> getTableQuery(
            @RequestParam(name = 'url', required = true) String url,
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @PathVariable(name = 'database', required = true) String database,
            @RequestParam(name = 'query', required = true) String query) {
        def result = tableDataService.query(url, instanceId, database, query)
        return ResponseEntity.ok().body(result)
    }

    @ApiOperation(value = 'Insert the Data using query')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Retrieve Table Insert Count'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/insert')
    ResponseEntity<?> insertTableDataQuery(
            @RequestParam(name = 'url', required = true) String url,
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @PathVariable(name = 'database', required = true) String database,
            @RequestParam(name = 'query', required = true) String query) {
        def result = tableDataService.upsertUsingDml(url, instanceId, database, query)
        return ResponseEntity.ok().body(new ResponseWrapper(message: 'Successfully Inserted Data Count : ' + result, status: 'success'))
    }

    @ApiOperation(value = 'Update the Data using query')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Update the table'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/update')
    ResponseEntity<?> updateTableDataUsingQuery(
            @RequestParam(name = 'url', required = true) String url,
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @PathVariable(name = 'database', required = true) String database,
            @RequestParam(name = 'query', required = true) String query) {
        def result = tableDataService.upsertUsingDml(url, instanceId, database, query)
        return ResponseEntity.ok().body(new ResponseWrapper(message: 'Successfully Updated Data Count : ' + result, status: 'success'))
    }

    @ApiOperation(value = 'Delete the data using query')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully deleted records count'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @DeleteMapping('/delete')
    ResponseEntity<?> dropDatabase(
            @RequestParam(name = 'url', required = true) String url,
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @PathVariable(name = 'database', required = true) String database,
            @RequestParam(name = 'query', required = true) String query) {
        def result = tableDataService.deleteUsingDml(url, instanceId, database, query)
        return ResponseEntity.ok().body(new ResponseWrapper(message: 'Successfully Deleted Rows : ' + result, status: 'success'))
    }
}

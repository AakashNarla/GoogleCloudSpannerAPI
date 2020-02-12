package com.google.spanner.controller

import com.google.cloud.spanner.Instance
import com.google.cloud.spanner.InstanceConfig
import com.google.spanner.object.ResponseWrapper
import com.google.spanner.service.InstanceAdminService
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping('/v1/spanner/instance/')
@Api(tags = ['Instance'], description = 'Spanner Instance API')
@Slf4j
class InstanceAdminController {

    @Autowired
    InstanceAdminService spannerService

    @ApiOperation(value = 'Return All Spanner Instance Config Data')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Get All Instance Config'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/configs')
    ResponseEntity<List<InstanceConfig>> getAllInstanceConfigs(@RequestParam(name = 'url', required = true) String url) {
        List<InstanceConfig> result = spannerService.listInstanceConfigs(url)
        return ResponseEntity.ok().body(result)
    }

    @ApiOperation(value = 'Return All Spanner Instance')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Get All Instance Config'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/')
    ResponseEntity<?> getAllInstance(@RequestParam(name = 'url', required = true) String url) {
        List<Instance> result = spannerService.listInstances(url)
        return ResponseEntity.ok().body(new JsonBuilder(result).toPrettyString())
    }


    @ApiOperation(value = 'Create new Spanner instance')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully created a new instance'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/create')
    ResponseEntity<ResponseWrapper> createInstance(
            @RequestParam(name = 'url', required = true) String url,
            @RequestParam(name = 'instance-name', required = true) String instanceName,
            @RequestParam(name = 'instance-config', required = true) String instanceConfig,
            @RequestParam(name = 'node-count', required = true) int nodeCount) {
        String message = spannerService.createInstance(url, instanceName, instanceConfig, nodeCount)
        return ResponseEntity.ok().body(new ResponseWrapper(message: message, status: 'success'))
    }

    @ApiOperation(value = 'Update an existing Spanner instance display name and node count')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully updated instance'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/{instance-id}/update')
    ResponseEntity<ResponseWrapper> updateInstance(
            @RequestParam(name = 'url', required = true) String url,
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @RequestParam(name = 'display-name', required = true) String newDisplayName,
            @RequestParam(name = 'node-count', required = true) int nodeCount) {
        String result = spannerService.updateInstance(url, instanceId, newDisplayName, nodeCount)
        return ResponseEntity.ok().body(new ResponseWrapper(message: result, status: 'success'))
    }

    @ApiOperation(value = 'Return Spanner instance state')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Return instance state'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/{instance-id}')
    ResponseEntity<?> getInstanceState(@PathVariable(name = 'instance-id', required = true) String instanceId,
                                       @RequestParam(name = 'url', required = true) String url) {
        Instance result = spannerService.getInstance(url, instanceId)
        return ResponseEntity.ok().body(new JsonBuilder(result).toPrettyString())
    }

    @ApiOperation(value = 'Return Spanner instance Config')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Instance Config Details'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @PostMapping('/{instance-config}/config')
    ResponseEntity<?> getInstanceConfig(@PathVariable(name = 'instance-config', required = true) String instanceConfig,
                                        @RequestParam(name = 'url', required = true) String url) {
        def result = spannerService.getInstanceConfig(url, instanceConfig)
        return ResponseEntity.ok().body(new JsonBuilder(result).toPrettyString())
    }

    @ApiOperation(value = 'Delete Spanner instance')
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = 'Successfully Deleted Spanner Instance'),
            @ApiResponse(code = 401, message = 'You are not authorized to view the resource'),
            @ApiResponse(code = 403, message = 'Accessing the resource you were trying to reach is forbidden'),
            @ApiResponse(code = 404, message = 'The resource you were trying to reach is not found')
    ]
    )
    @DeleteMapping('/{instance-id}/delete')
    ResponseEntity<ResponseWrapper> deleteInstance(
            @PathVariable(name = 'instance-id', required = true) String instanceId,
            @RequestParam(name = 'url', required = true) String url) {
        String result = spannerService.deleteInstance(url, instanceId)
        return ResponseEntity.ok().body(new ResponseWrapper(message: result, status: 'success'))
    }
}

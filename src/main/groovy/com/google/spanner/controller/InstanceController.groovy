package com.google.spanner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.annotations.*;
import com.google.spanner.service.SpannerService
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api

@RestController
@RequestMapping("/v1/spanner/instance/")
@Api(tags = ["Instance"], description = "Spanner Instance API")
@Slf4j
class InstanceController {

	@Autowired
	SpannerService spannerService

	@ApiOperation(value = "Return Spanner instance state")
	@ApiResponses(value = [
		@ApiResponse(code = 200, message = "Successfully instance state"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	]
	)
	@GetMapping("{access_token}/{project-id}/{instance-id}/")
	String getInstanceState(@PathVariable(name = "access_token", required = true)String accessToken, @PathVariable(name = "project-id", required = true)String projectId, @PathVariable(name = "instance-id", required = true)String instanceId){
		String result = spannerService.getInstanceState(accessToken, projectId, instanceId)
		return result
	}
}

package com.google.spanner.exception

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

import groovy.util.logging.Slf4j

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionControllerAdvice {

	@ExceptionHandler(ResourceNotFoundException)
	@ResponseStatus(code= HttpStatus.NOT_FOUND)
	public ErrorObject resourceNotFoundException(final ResourceNotFoundException e) {
		log.error("Global Error Caught Exception : {}", e)
		return new ErrorObject(message: e?.message, status: e?.httpStatusCode, detail: e?.detailedMessage)
	}

	@ExceptionHandler(IllegalArgumentException)
	public ErrorObject assertionException(final IllegalArgumentException e) {
		return new ErrorObject(e, HttpStatus.NOT_FOUND, e.getLocalizedMessage())
	}
}
package com.google.spanner.exception

import com.google.cloud.spanner.SpannerException
import groovy.util.logging.Slf4j
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    ErrorObject resourceNotFoundException(final ResourceNotFoundException e) {
        log.error('Global Error Caught Exception : {}', e)
        return new ErrorObject(message: e?.message, status: e?.httpStatusCode, detail: e?.detailedMessage)
    }

    @ExceptionHandler(SpannerException)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorObject internalServerError(final SpannerException e) {
        log.error('SpannerException Caught Exception : {}, error code : {}', e, e?.getErrorCode())
        return new ErrorObject(message: e?.getErrorCode(), status: 500, detail: e?.message)
    }

    @ExceptionHandler(UnknownException)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorObject unknownExceptionError(final UnknownException e) {
        //log.error('UnknownException Caught Exception : {}, error code : {}', e, e?.getErrorCode())
        return new ErrorObject(message: e?.message, status: e?.httpStatusCode, detail: e?.detailedMessage)
    }

    @ExceptionHandler(IllegalArgumentException)
    ErrorObject assertionException(final IllegalArgumentException e) {
        return new ErrorObject(e?.message, HttpStatus.NOT_FOUND.value(), e.getLocalizedMessage())
    }
}
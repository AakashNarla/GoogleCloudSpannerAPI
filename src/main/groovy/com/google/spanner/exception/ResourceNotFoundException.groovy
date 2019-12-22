package com.google.spanner.exception

class ResourceNotFoundException extends Exception {
	String message
	String detailedMessage
	int httpStatusCode

	ResourceNotFoundException(String message, int httpStatusCode, String detailedMessage){
		this(message, httpStatusCode, detailedMessage, null)
	}

	ResourceNotFoundException(String message, int httpStatusCode, String detailedMessage, Exception e){
		super(e)
		this.message = message
		this.detailedMessage = detailedMessage
		this.httpStatusCode = httpStatusCode
	}
}

package com.google.spanner.exception

class CredentialsNotFoundException extends Exception {
	String message
	String detailedMessage
	String httpStatusCode

	CredentialsNotFoundException(String message, String httpStatusCode, String detailedMessage){
		this(message, httpStatusCode, detailedMessage, null)
	}

	CredentialsNotFoundException(String message, String httpStatusCode, String detailedMessage, Exception e){
		super(e)
		this.message = message
		this.detailedMessage = detailedMessage
		this.httpStatusCode = httpStatusCode
	}
}

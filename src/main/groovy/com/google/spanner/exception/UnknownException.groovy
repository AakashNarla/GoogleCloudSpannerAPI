package com.google.spanner.exception

class UnknownException extends Exception {
    String message
    String detailedMessage
    int httpStatusCode

    UnknownException(String message, int httpStatusCode, String detailedMessage) {
        this(message, httpStatusCode, detailedMessage, null)
    }

    UnknownException(String message, int httpStatusCode, String detailedMessage, Exception e) {
        super(e)
        this.message = message
        this.detailedMessage = detailedMessage
        this.httpStatusCode = httpStatusCode
    }
}

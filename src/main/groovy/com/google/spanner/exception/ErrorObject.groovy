package com.google.spanner.exception

import groovy.transform.Canonical

@Canonical
class ErrorObject {
	String message
	int status
	String detail
}

package com.google.spanner.object

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ResponseWrapper {
    String message
    String status = "success"
    Double count = null
}

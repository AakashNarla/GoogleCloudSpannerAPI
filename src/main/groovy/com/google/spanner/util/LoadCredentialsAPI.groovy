package com.google.spanner.util

import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.annotation.Retryable

import java.security.InvalidKeyException
import java.security.spec.InvalidKeySpecException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.JsonParser
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.auth.http.HttpTransportFactory
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.DatabaseAdminClient
import com.google.cloud.spanner.DatabaseClient
import com.google.cloud.spanner.DatabaseId
import com.google.cloud.spanner.InstanceAdminClient
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions
import com.google.spanner.exception.ResourceNotFoundException
import com.google.spanner.exception.UnknownException

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Component
@Slf4j
@EnableRetry
class LoadCredentialsAPI {
    @Retryable(
            value = [ Exception ],
            maxAttempts = 2,
            backoff = @Backoff(delay = 500L))
    private static InputStream getInputStreamURL(String url) {
        InputStream inputStream
        try {
            URL urlObject = new URL(url)
            URLConnection urlConnection = urlObject.openConnection()
            inputStream = urlConnection.getInputStream()
        } catch (Exception e) {
            log.error("Exception Loading the file {} ", e.getMessage())
            throw new ResourceNotFoundException("Credentials Missing", HttpStatus.NOT_FOUND.value(), "Credentials Not Found for Url Provided", e)
        }
        return inputStream
    }

    static GoogleCredentials createGoogleCredentials(String url) {
        InputStream inp = getInputStreamURL(url)
        return GoogleCredentials.fromStream(inp)
    }

    static String getProjectFromCredentials(String url){
        InputStream ins = getInputStreamURL(url)
        def json = new JsonSlurper().parse(ins)
        return json?.project_id
    }

    static Spanner getSpanner(String url) {
        Spanner spanner
        try {
            GoogleCredentials credentials = createGoogleCredentials(url)
            if (!credentials?.clientId) {
                throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), "Client Id Not Found, Please recheck service account key", new IOException())
            }
            spanner = SpannerOptions.newBuilder().setCredentials(credentials).build().getService()
        } catch (IOException e) {
            throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), e?.message, e)
        } catch (Exception e) {
            throw new UnknownException("Unknown Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), e?.message, e)
        }

        return spanner
    }
}

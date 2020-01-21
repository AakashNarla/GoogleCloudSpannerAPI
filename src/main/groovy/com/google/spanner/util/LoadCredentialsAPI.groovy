package com.google.spanner.util

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerOptions
import com.google.spanner.exception.ResourceNotFoundException
import com.google.spanner.exception.UnknownException
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
@Slf4j
@EnableRetry
class LoadCredentialsAPI {

    @Retryable(
            value = [ Exception ],
            maxAttempts = 3,
            backoff = @Backoff(delay = 500L))
     InputStream getInputStreamURL(String url) {
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

    @Recover
    InputStream recover(String url){
        log.error("Entered Recover method: Failed maximum time to load the file {}", url)
        return null
    }

    GoogleCredentials createGoogleCredentials(String url) {
        InputStream inp = getInputStreamURL(url)
        log.info("Loading Google Credentials from URL : {} ", url)
        return GoogleCredentials.fromStream(inp)
    }

     String getProjectFromCredentials(String url){
        InputStream ins = getInputStreamURL(url)
        def json = new JsonSlurper().parse(ins)
        return json?.project_id
    }

    Spanner getSpanner(String url) {
        Spanner spanner
        try {
            GoogleCredentials credentials = createGoogleCredentials(url)
            if (!credentials?.clientId) {
                log.error("Credentials Error: Client Id Not Found, Please recheck service account key")
                throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), "Client Id Not Found, Please recheck service account key", new IOException())
            }
            log.info("Google Credentials: Login Successfully for Client Id: {} ", credentials?.clientId)
            spanner = SpannerOptions.newBuilder().setCredentials(credentials).build().getService()
        } catch (IOException e) {
            throw new ResourceNotFoundException("Credentials Error", HttpStatus.NOT_FOUND.value(), e?.message, e)
        } catch (Exception e) {
            throw new UnknownException("Unknown Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), e?.message, e)
        }

        return spanner
    }
}

package intg;

import com.google.spanner.GcpSpannerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootTest(classes = GcpSpannerApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration
public class SpringIntegrationTest {
    static ResponseResults latestResponse = null;
    static ResponseEntity<String> postResponse = null;
    static String responseString  = null;

    protected RestTemplate restTemplate;

    void executeGet(String url) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        final ResponseResultErrorHandler errorHandler = new ResponseResultErrorHandler();

        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        restTemplate.setErrorHandler(errorHandler);
        postResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class);
        System.out.println(postResponse.getBody() + postResponse.getStatusCode());
        responseString = convertResponseToString(postResponse);
    }

    void executePost(String url, MultiValueMap queryParams, Map params) {

        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromUriString(url)
                .buildAndExpand(params)
                .toUri();

        uri = UriComponentsBuilder
                .fromUri(uri)
                .queryParams(queryParams)
                .queryParam("url", GlobalConfig.credentialsUrl)
                .build()
                .toUri();

        postResponse = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                String.class);
        System.out.println(postResponse.getBody() + postResponse.getStatusCode());
        responseString = postResponse.getBody();
        //responseString = convertResponseToString(postResponse);
    }

    void executeDelete(String url, Map params) {

        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);


        URI uri = UriComponentsBuilder.fromUriString(url)
                .buildAndExpand(params)
                .toUri();

        uri = UriComponentsBuilder
                .fromUri(uri)
                .queryParam("url", GlobalConfig.credentialsUrl)
                .build()
                .toUri();


        postResponse = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                entity,
                String.class);
        System.out.println(postResponse.getBody() + postResponse.getStatusCode());
        responseString = postResponse.getBody();
        //responseString = convertResponseToString(postResponse);
    }

    private class ResponseResultErrorHandler implements ResponseErrorHandler {
        private ResponseResults results = null;
        private Boolean hadError = false;

        private ResponseResults getResults() {
            return results;
        }

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            hadError = response.getRawStatusCode() >= 400;
            return hadError;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            results = new ResponseResults(response);
        }
    }

    private String convertResponseToString(ResponseEntity<String> response) throws IOException {
        String responseBody = response.getBody();
        Scanner scanner = new Scanner(responseBody);
        String responseString = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return responseString;
    }

    Map createURLParams(String instanceId, String databaseName, String tableName ) {
        HashMap<String, String> params = new HashMap<>();
        params.put("instance-id", instanceId);
        params.put("database-name", databaseName);
        params.put("table-name", tableName);

        return params;
    }

    MultiValueMap queryParamsForCreate(String instanceName, String instanceConfigId, int nodeCount ) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("instance-name", instanceName);
        params.add("instance-config", instanceConfigId);
        params.add("node-count", nodeCount);

        return params;
    }

    MultiValueMap queryParamsForUpdate(String displayName, int nodeCount ) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("display-name", displayName);
        params.add("node-count", nodeCount);

        return params;
    }
}
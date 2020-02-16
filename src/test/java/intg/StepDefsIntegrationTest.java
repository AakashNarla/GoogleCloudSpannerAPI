package intg;

import com.google.spanner.util.MessageMatcher;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StepDefsIntegrationTest extends SpringIntegrationTest {


    @Given("^the client calls /actuator$")
    public void the_client_issues_GET_hello() throws Throwable {
        executeGet("http://localhost:8080/actuator");
    }

    @Given("^the client calls /actuator/info$")
    public void the_client_actuator_hello() throws Throwable {
        executeGet("http://localhost:8080/actuator/info");
    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        final HttpStatus currentStatusCode = postResponse.getStatusCode();
        assertThat("status code is incorrect : " + postResponse.getBody(), currentStatusCode.value(), is(statusCode));
    }

    @And("^the client receives server version (.+)$")
    public void the_client_receives_server_version_body(String version) throws Throwable {
        Assert.assertTrue(new MessageMatcher().matchJsonString(responseString, version));
        //assertThat(responseString, containsString(version));
    }

    @Given("^the client calls /configs:$")
    public void getAllInstancesConfig() throws Throwable {
        String url = GlobalConfig.baseInstanceUrl.concat("/configs");
        Map params = createURLParams("spanner-demo",null,null);
        executePost(url,null, params);
    }

    @Given("^create new spanner instance:$")
    public void createNewInstance() throws Throwable {
        String url = GlobalConfig.baseInstanceUrl.concat("/create");
        Map params = createURLParams("spanner-demo",null,null);
        MultiValueMap queryParams = queryParamsForCreate("spanner-demo","regional-asia-south1",1);
        executePost(url, queryParams, params);
    }

    @Given("^get current spanner instance:$")
    public void getCurrInstanceState() throws Throwable {
        String url = GlobalConfig.baseInstanceUrl.concat("/{instance-id}");
        Map params = createURLParams("spanner-demo",null,null);
        executePost(url,null, params);
    }

    @Given("^get All spanner instance:$")
    public void getAllInstancesSpannerInstances() throws Throwable {
        String url = GlobalConfig.baseInstanceUrl;
        Map params = createURLParams(null,null,null);
        executePost(url,null, params);
    }

    @Given("^update spanner instance:$")
    public void updateCurrInstanceState() throws Throwable {
        String url = GlobalConfig.baseInstanceUrl.concat("/{instance-id}/update");
        Map params = createURLParams("spanner-demo",null,null);
        MultiValueMap queryParams = queryParamsForUpdate("spanner-demo-1",1);
        executePost(url,queryParams, params);
    }

    @Given("^delete spanner instance:$")
    public void deleteSpannerInstance() throws Throwable {
        String url = GlobalConfig.baseInstanceUrl.concat("/{instance-id}/delete");
        Map params = createURLParams("spanner-demo",null,null);
        executeDelete(url, params);
    }

    //Database API Steps

    @Given("^create new database:$")
    public void createNewDatabase() throws Throwable {
        String url = GlobalConfig.baseDatabaseUrl.concat("/create/{database-name}");
        Map params = createURLParams("spanner-demo","spanner",null);
         executePost(url, null, params);
    }

    @Given("^get current database status:$")
    public void getCurrDatabaseState() throws Throwable {
        String url = GlobalConfig.baseDatabaseUrl.concat("/{database-name}/status");
        Map params = createURLParams("spanner-demo","spanner",null);
        executePost(url,null, params);
    }

    @Given("^delete database:$")
    public void deleteDatabase() throws Throwable {
        String url = GlobalConfig.baseDatabaseUrl.concat("/drop/{database-name}");
        Map params = createURLParams("spanner-demo","spanner",null);
        executeDelete(url, params);
    }
}

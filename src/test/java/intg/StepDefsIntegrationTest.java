package intg;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import cucumber.api.java.en.Given;
import org.springframework.http.HttpStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

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
        final HttpStatus currentStatusCode = latestResponse.getTheResponse().getStatusCode();
        assertThat("status code is incorrect : " + latestResponse.getBody(), currentStatusCode.value(), is(statusCode));
    }

    @And("^the client receives server version (.+)$")
    public void the_client_receives_server_version_body(String version) throws Throwable {
        assertThat(latestResponse.getBody(), is(version));
    }
}

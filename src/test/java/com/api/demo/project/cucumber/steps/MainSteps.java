package com.api.demo.project.cucumber.steps;

import com.api.demo.project.helpers.PayloadBuilder;
import com.api.demo.project.requests.UserRequests;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MainSteps {

    @Autowired
    private PayloadBuilder payloadBuilder;

    @Autowired
    private UserRequests userRequests;

    private static String token;
    private Response response;
    private String requestBody;
    private int createdUserId;

    @Given("I prepare the request payload {string}")
    public void prepareRequestPayload(String fileName) {
        requestBody = payloadBuilder.prepareRequestPayload(fileName);
        requestBody = applyRandomEmailIfNecessary(requestBody);
    }

    private String applyRandomEmailIfNecessary(String body) {
        if (body != null && body.contains("{{randomEmail}}")) {
            String randomEmail = "user" + System.currentTimeMillis() + "@example.com";
            return body.replace("{{randomEmail}}", randomEmail);
        }
        return body;
    }

    @When("I send a POST request to the token endpoint")
    public void postTokenRequest() {
        response = userRequests.postTokenRequest(requestBody);

        if (response.getStatusCode() == 200) {
            token = response.jsonPath().getString("token");
            assertNotNull("Token must not be null after successful login", token);
        }
    }

    @When("I send a POST request to the users endpoint")
    public void postUserRequest() {
        response = userRequests.postUserRequest(token, requestBody);
    }

    @When("I send a GET request to {string}")
    public void getUsersRequest(String endpoint) {
        response = userRequests.getUsersRequest(token);
    }

    @When("I send a DELETE request to the user")
    public void deleteUserRequest() {
        response = userRequests.deleteUserByIdRequest(token, createdUserId);
    }

    @When("I send a GET request to the user by id")
    public void getUserByIdRequest() {
        response = userRequests.getUserByIdRequest(token, createdUserId);
    }

    @When("I send a PUT request to update the user")
    public void putUserRequest() {
        response = userRequests.updateUserByIdRequest(token, createdUserId, requestBody);
    }

    @Then("the response status code should be {int}")
    public void checkStatusCode(int expectedStatus) {
        assertEquals(
                "Expected status code " + expectedStatus + " but got " + response.getStatusCode() + ". Body: " + response.asString(),
                expectedStatus,
                response.getStatusCode()
        );
    }

    @Then("the response contains a list of users")
    public void checkUsersList() {
        List<Map<String, Object>> users = response.jsonPath().getList("$");
        assertNotNull("Response body must not be null", users);
        assertFalse("Users list must not be empty", users.isEmpty());
    }

    @Then("I save the user ID from the response")
    public void saveUserId() {
        createdUserId = response.jsonPath().getInt("id");
        assertTrue("User ID must be greater than 0", createdUserId > 0);
    }
}
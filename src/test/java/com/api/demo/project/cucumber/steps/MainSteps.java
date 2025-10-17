package com.api.demo.project.cucumber.steps;

import com.api.demo.project.helpers.PayloadBuilder;
import com.api.demo.project.requests.UserRequests;
import com.api.demo.project.storage.ScenarioStorage;
import io.cucumber.java.en.*;
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

    @Autowired
    private ScenarioStorage storage;

    private Response response;

    @Given("I prepare the request payload {string}")
    public void prepareRequestPayload(String fileName) {
        String body = payloadBuilder.prepareRequestPayload(fileName);
        body = applyRandomEmailIfNecessary(body);
        storage.setRequestBody(body);
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
        response = userRequests.postTokenRequest(storage.getRequestBody());

        if (response.getStatusCode() == 200) {
            storage.setToken(response.jsonPath().getString("token"));
            assertNotNull("Token must not be null after successful login", storage.getToken());
        }
    }

    @When("I send a POST request to the users endpoint")
    public void postUserRequest() {
        response = userRequests.postUserRequest(storage.getToken(), storage.getRequestBody());
    }

    @When("I send a GET request to {string}")
    public void getUsersRequest(String endpoint) {
        response = userRequests.getUsersRequest(storage.getToken());
    }

    @When("I send a DELETE request to the user")
    public void deleteUserRequest() {
        response = userRequests.deleteUserByIdRequest(storage.getToken(), storage.getUserId());
    }

    @When("I send a GET request to the user by id")
    public void getUserByIdRequest() {
        response = userRequests.getUserByIdRequest(storage.getToken(), storage.getUserId());
    }

    @When("I send a PUT request to update the user")
    public void putUserRequest() {
        response = userRequests.updateUserByIdRequest(storage.getToken(), storage.getUserId(), storage.getRequestBody());
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
        int id = response.jsonPath().getInt("id");
        assertTrue("User ID must be greater than 0", id > 0);
        storage.setUserId(id);
    }
}

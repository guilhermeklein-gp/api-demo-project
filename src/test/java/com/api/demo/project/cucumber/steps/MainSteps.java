package com.api.demo.project.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class MainSteps {

    private static String token;
    private Response response;
    private String requestBody;
    private String loadJson(String fileName) throws IOException {
        String path = "src/main/java/com/api/demo/project/payloads/" + fileName;
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    @Given("I prepare the request payload {string}")
    public void i_prepare_the_request_payload(String fileName) throws IOException {
        requestBody = loadJson(fileName);

        if(requestBody.contains("{{randomEmail}}")) {
            String randomEmail = "user" + System.currentTimeMillis() + "@example.com";
            requestBody = requestBody.replace("{{randomEmail}}", randomEmail);
        }

        System.out.println("Payload:" + fileName);
        System.out.println(requestBody);
    }

    @When("I send a POST request to the token endpoint")
    public void i_send_a_post_request_to_the_token_endpoint() {
        baseURI = "https://prosurgical-billi-devastatingly.ngrok-free.dev";

        response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/get-token");

        response.then().log().all();

        assertEquals("Fail", 200, response.getStatusCode());
        token = response.jsonPath().getString("token");
        assertNotNull("Null", token);
        System.out.println("Success: " + token);
    }

    @When("I send a POST request to the users endpoint")
    public void i_send_a_post_request_to_the_users_endpoint() {
        baseURI = "https://prosurgical-billi-devastatingly.ngrok-free.dev";

        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/users");

        response.then().log().all();
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatus) {
        int actualStatus = response.getStatusCode();
        Assert.assertEquals(
                "Expected status code " + expectedStatus + " but got " + actualStatus,
                expectedStatus,
                actualStatus
        );
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        baseURI = "https://prosurgical-billi-devastatingly.ngrok-free.dev";

        response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint);

        response.then().log().all();
    }

    @Then("the response contains a list of users")
    public void the_response_contains_a_list_of_users() {
        List<Map<String, Object>> users = response.jsonPath().getList("$");
        Assert.assertTrue("Null", users.size() > 0);

        users.forEach(user -> System.out.println(user));
    }

    private int createdUserId;

    @Then("I save the user ID from the response")
    public void i_save_the_user_id_from_the_response() {
        createdUserId = response.jsonPath().getInt("id");
        System.out.println("Created user ID: " + createdUserId);
    }

    @When("I send a DELETE request to the user")
    public void i_send_a_delete_request_to_the_user() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .delete("/users/" + createdUserId);
        response.then().log().all();
    }

    @When("I send a GET request to the user by id")
    public void i_send_a_get_request_to_the_user_by_id() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .get("/users/" + createdUserId);
        response.then().log().all();
    }
}

package com.api.demo.project.requests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.*;

@Component
public class UserRequests {

    private final String tokenEndpoint;
    private final String usersEndpoint;
    private final String usersByIdEndpoint;

    public UserRequests(
            @Value("${URL}") String baseUrl,
            @Value("${TOKEN}") String tokenEndpoint,
            @Value("${USERS}") String usersEndpoint,
            @Value("${USERS_BY_ID}") String usersByIdEndpoint) {

        baseURI = baseUrl;
        this.tokenEndpoint = tokenEndpoint;
        this.usersEndpoint = usersEndpoint;
        this.usersByIdEndpoint = usersByIdEndpoint;
    }

    private RequestSpecification baseRequest() {
        return given().contentType(ContentType.JSON);
    }

    private RequestSpecification authenticatedRequest(String token) {
        return baseRequest().header("Authorization", "Bearer " + token);
    }

    public Response postTokenRequest(String requestBody) {
        return baseRequest()
                .body(requestBody)
                .post(tokenEndpoint);
    }

    public Response postUserRequest(String token, String requestBody) {
        return authenticatedRequest(token)
                .body(requestBody)
                .post(usersEndpoint);
    }

    public Response getUsersRequest(String token) {
        return authenticatedRequest(token)
                .get(usersEndpoint);
    }

    public Response getUserByIdRequest(String token, int userId) {
        return authenticatedRequest(token)
                .pathParam("id", userId)
                .get(usersByIdEndpoint);
    }

    public Response updateUserByIdRequest(String token, int userId, String requestBody) {
        return authenticatedRequest(token)
                .pathParam("id", userId)
                .body(requestBody)
                .put(usersByIdEndpoint);
    }

    public Response deleteUserByIdRequest(String token, int userId) {
        return authenticatedRequest(token)
                .pathParam("id", userId)
                .delete(usersByIdEndpoint);
    }
}

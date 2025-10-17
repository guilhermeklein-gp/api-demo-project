package com.api.demo.project.requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@Component
public class UserRequests {

    private final String TOKEN_ENDPOINT;
    private final String USERS_ENDPOINT;
    private final String USERS_BY_ID_ENDPOINT;

    public UserRequests(@Value("${URL}") String url,
                        @Value("${TOKEN}") String tokenEndpoint,
                        @Value("${USERS}") String usersEndpoint,
                        @Value("${USERS_BY_ID}") String usersByIdEndpoint) {
        baseURI = url;
        this.TOKEN_ENDPOINT = tokenEndpoint;
        this.USERS_ENDPOINT = usersEndpoint;
        this.USERS_BY_ID_ENDPOINT = usersByIdEndpoint;
    }

    private RequestSpecification authenticatedRequest(String token) {
        return given()
                .header("Authorization", "Bearer " + token);
    }

    public Response postTokenRequest(String requestBody) {
        return given()
                .contentType("application/json")
                .body(requestBody)
                .post(TOKEN_ENDPOINT);
    }

    public Response postUserRequest(String token, String requestBody) {
        return authenticatedRequest(token)
                .contentType("application/json")
                .body(requestBody)
                .post(USERS_ENDPOINT);
    }

    public Response getUsersRequest(String token) {
        return authenticatedRequest(token)
                .get(USERS_ENDPOINT);
    }

    public Response deleteUserByIdRequest(String token, int userId) {
        return authenticatedRequest(token)
                .pathParam("id", userId)
                .delete(USERS_BY_ID_ENDPOINT);
    }

    public Response updateUserByIdRequest(String token, int userId, String requestBody) {
        return authenticatedRequest(token)
                .contentType("application/json")
                .pathParam("id", userId)
                .body(requestBody)
                .put(USERS_BY_ID_ENDPOINT);
    }

    public Response getUserByIdRequest(String token, int userId) {
        return authenticatedRequest(token)
                .pathParam("id", userId)
                .get(USERS_BY_ID_ENDPOINT);
    }
}
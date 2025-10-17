Feature: User management

  Background:
    Given I prepare the request payload "auth.json"
    When I send a POST request to the token endpoint
    Then the response status code should be 200

  Scenario: Create a new user and update
    Given I prepare the request payload "newUser.json"
    When I send a POST request to the users endpoint
    Then the response status code should be 201
    And I save the user ID from the response

    # Updating the new user created
    Given I prepare the request payload "updateUser.json"
    When I send a PUT request to update the user
    Then the response status code should be 200

    # Delete the created user to avoid polluting the database
    When I send a DELETE request to the user
    Then the response status code should be 200

  Scenario: Get all users
    When I send a GET request to "/users"
    Then the response status code should be 200
    And the response contains a list of users

  Scenario: Delete a user and Search by ID
    Given I prepare the request payload "newUser.json"
    When I send a POST request to the users endpoint
    Then the response status code should be 201

    And I save the user ID from the response

    When I send a DELETE request to the user
    Then the response status code should be 200

    When I send a GET request to the user by id
    Then the response status code should be 404
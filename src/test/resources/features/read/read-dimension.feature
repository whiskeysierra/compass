Feature: Reading dimension

  Scenario: Read dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    Then "GET /dimensions/device" responds "200 OK" with:
      | /id      | /schema/type | /relation | /description |
      | "device" | "string"     | "="       | ".."         |

  Scenario: Read unknown dimension
    Then "GET /dimensions/unknwon" responds "404 Not Found"

  Scenario: Read deleted dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "DELETE /dimensions/device" responds "204 No Content"
    Then "GET /dimensions/device" responds "410 Gone" with headers:
      | Location                                            |
      | http://localhost:8080/dimensions/device/revisions/2 |

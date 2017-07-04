Feature: Read key

  Scenario: Read key
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "GET /keys/tax-rate" responds "200 OK" with:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |

  Scenario: Read unknown key
    Then "GET /keys/unknown" responds "404 Not Found"

  Scenario: Read deleted key
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "DELETE /keys/tax-rate" responds "204 No Content"
    Then "GET /keys/tax-rate" responds "410 Gone" with headers:
      | Location                                        |
      | http://localhost:8080/keys/tax-rate/revisions/2 |

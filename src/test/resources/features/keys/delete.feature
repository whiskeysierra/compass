Feature: Key deletion

  Scenario: Delete key
    Given "PUT /keys/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    When "DELETE /keys/feature.active" responds "204 No Content"
    When "GET /keys" responds "200 OK" with an array at "/keys":
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |

  Scenario: Deleting unknown key fails
    Given "GET /keys/example" responds "404 Not Found"
    Then "DELETE /keys/example" responds "404 Not Found"

  Scenario: Delete used key
    Given "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /value |
      | 0.16   |
    Then "DELETE /keys/tax-rate" responds "204 No Content"

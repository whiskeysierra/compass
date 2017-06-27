Feature: Key deletion

  Scenario: Delete key
    Given "PUT /keys/{id}" (using /id) always returns "201 Created" when requested individually with:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    When "DELETE /keys/feature.active" returns "204 No Content"
    When "GET /keys" returns "200 OK" with a list of /keys:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |

  Scenario: Deleting unknown key fails
    Given "GET /keys/example" returns "404 Not Found"
    Then "DELETE /keys/example" returns "404 Not Found"

  Scenario: Delete used key
    Given "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /value |
      | 0.16   |
    Then "DELETE /keys/tax-rate" returns "204 No Content"

Feature: Key deletion

  Scenario: Delete key
    Given the following keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    When "DELETE /keys/feature.active" returns "204 No Content"
    When "GET /keys" returns "200 OK" with a list of /keys:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |

  Scenario: Deleting unknown key fails
    Given there are no keys
    Then "DELETE /keys/example" returns "404 Not Found"

  Scenario: Delete used key
    Given the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /value |
      | 0.16   |
    Then "DELETE /keys/tax-rate" returns "204 No Content"

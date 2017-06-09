Feature: Value creation

  Scenario: Creating a new value
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns an empty list of values
    When "POST /keys/tax-rate/values" is requested with this it returns "201 Created":
      | value |
      | 0.19  |
    Then "GET /keys/tax-rate/values" returns a list of values:
      | dimensions | value |
      | {}         | 0.19  |

  Scenario: Creating a new value failed due to schema violation
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns an empty list of values
    When "POST /keys/tax-rate/values" is requested with this:
      | value |
      | "19%" |
    Then it returns "400 Bad Request" with a list of violations:
      | field     | message                                  |
      | "$.value" | "$.value: string found, number expected" |

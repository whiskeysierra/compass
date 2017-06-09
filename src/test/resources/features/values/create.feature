Feature: Value creation

  Scenario: Creating a new value without dimensions
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "POST /keys/tax-rate/values" returns "201 Created" when requested with:
      | value |
      | 0.19  |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions | value |
      | {}         | 0.19  |

  Scenario: Creating a new value empty dimensions
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "POST /keys/tax-rate/values" returns "201 Created" when requested with:
      | dimensions | value |
      | {}         | 0.19  |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions | value |
      | {}         | 0.19  |

  Scenario: Creating a new value failed due to schema violation
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "POST /keys/tax-rate/values" when requested with:
      | value |
      | "19%" |
    Then "400 Bad Request" was returned with a list of violations:
      | field     | message                                  |
      | "$.value" | "$.value: string found, number expected" |

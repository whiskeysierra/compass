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

  Scenario: Canonical value URL
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
      | "before"  | "string"    | "<"      | "ISO 8601"           |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "POST /keys/tax-rate/values" when requested with:
      | dimensions.country | dimensions.before      | value |
      | "DE"               | "2007-01-01T00:00:00Z" | 0.16  |
    Then "201 Created" was returned with headers:
      | Location                                                                         |
      | http://localhost:8080/keys/tax-rate/value?before=2007-01-01T00:00:00Z&country=DE |

  # TODO verify Location header

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

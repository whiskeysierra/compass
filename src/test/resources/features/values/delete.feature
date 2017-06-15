Feature: Delete a value

  Scenario: Delete value without dimensions
    Given the following keys:
      | id               | schema.type | description |
      | "tax-rate"       | "number"    | ".."        |
      | "feature.active" | "boolean"   | ".."        |
    And the following values for key tax-rate:
      | value |
      | 0.19  |
    And the following values for key feature.active:
      | value |
      | true  |
    When "DELETE /keys/tax-rate/values" returns "204 No Content"
    Then "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    And "GET /keys/feature.active/values" returns "200 OK" with a list of values:
      | value |
      | true  |

  Scenario: Delete value with dimension
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
      | "feature.active" | "boolean"   | ".."        |
    And the following values for key tax-rate:
      | dimensions.country | value |
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |
    And the following values for key feature.active:
      | value |
      | true  |
    When "DELETE /keys/tax-rate/values?country=CH" returns "204 No Content"
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |
    And "GET /keys/feature.active/values" returns "200 OK" with a list of values:
      | value |
      | true  |

  Scenario: Delete value without matching dimensions should fail
    Given the following dimensions:
      | id       | schema.type | schema.format | relation | description |
      | "before" | "string"    | "date-time"   | "<"      | "ISO 8601"  |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | dimensions.before           | value |
      | "2007-01-01T00:00:00+02:00" | 0.16  |
    And "GET /keys/tax-rate/values?after=2006-12-31T23:59:59+01:00" returns "200 OK"
    When "DELETE /keys/tax-rate/values?after=2006-12-31T23:59:59+01:00" returns "404 Not Found"

  Scenario: Delete value with unknown dimension should fail
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | value |
      | 0.19  |
    When "DELETE /keys/tax-rate/values?foo=bar" returns "404 Not Found"

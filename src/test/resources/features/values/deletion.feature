Feature: Value deletion

  Scenario: Delete value
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | dimensions.country | value |
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |
    When "DELETE /keys/tax-rate/values?country=CH" returns "204 No Content"
    Then "GET /keys/tax-rate/values" returns a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |

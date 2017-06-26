Feature: Value update

  Scenario: Updating a value with dimensions
    Given the following dimensions:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "before"  | "string"     | "<"       | "ISO 8601"           |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "201 Created" when requested with:
      | /value |
      | 0.16   |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "200 OK" when requested with:
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of /values:
      | /value |
      | 0.19   |

Feature: Value update

  Scenario: Updating a value with dimensions
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "before"  | "string"     | "<"       | "ISO 8601"           |
    And "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /value |
      | 0.19   |

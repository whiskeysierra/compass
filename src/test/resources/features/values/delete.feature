Feature: Delete a value

  Scenario: Delete value without dimensions
    Given "PUT /keys/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id              | /schema/type | /description |
      | "tax-rate"       | "number"     | ".."         |
      | "feature.active" | "boolean"    | ".."         |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /value |
      | 0.19   |
    And "PUT /keys/feature.active/values" responds "201 Created" when requested with an array at "/values":
      | /value |
      | true   |
    When "DELETE /keys/tax-rate/values" responds "204 No Content"
    Then "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    And "GET /keys/feature.active/values" responds "200 OK" with an array at "/values":
      | /value |
      | true   |

  Scenario: Delete value with dimension
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id              | /schema/type | /description |
      | "tax-rate"       | "number"     | ".."         |
      | "feature.active" | "boolean"    | ".."         |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    And "PUT /keys/feature.active/values" responds "201 Created" when requested with an array at "/values":
      | /value |
      | true   |
    When "DELETE /keys/tax-rate/values?country=CH" responds "204 No Content"
    Then "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
    And "GET /keys/feature.active/values" responds "200 OK" with an array at "/values":
      | /value |
      | true   |

  Scenario: Delete value without matching dimensions should fail
    Given "PUT /dimensions/before" responds "201 Created" when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "<"       | "ISO 3166-1 alpha-2" |
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/before          | /value |
      | "2007-01-01T00:00:00+02:00" | 0.16   |
    And "GET /keys/tax-rate/values?after=2006-12-31T23:59:59+01:00" responds "200 OK"
    When "DELETE /keys/tax-rate/values?after=2006-12-31T23:59:59+01:00" responds "404 Not Found"

  Scenario: Delete value with unknown dimension should fail
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /value |
      | 0.19   |
    When "DELETE /keys/tax-rate/values?foo=bar" responds "404 Not Found"

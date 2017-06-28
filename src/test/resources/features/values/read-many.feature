Feature: Read values

  Scenario: List empty values
    Given "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    Then "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"

  Scenario: List values
    Given "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /value |
      | 0.19   |
    When "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /value |
      | 0.19   |

  Scenario: List values with dimensions
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "active"  | "boolean"    | "="       | ".."                 |
      | "age"     | "number"     | "<="      | ".."                 |
    And "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /dimensions/country | /dimensions/active | /dimensions/age | /value |
      | "DE"                | false              | 16              | 0.16   |
      | "DE"                | true               | 32              | 0.19   |
    Then "GET /keys/tax-rate/values?country=DE&active=true&age=27" responds "200 OK" with an array at "/values":
      | /dimensions/country | /dimensions/active | /dimensions/age | /value |
      | "DE"                | true               | 32              | 0.19   |

  Scenario: Read values from non-existing key should fail
    Then "GET /keys/tax-rate/values" responds "404 Not Found"

  # TODO make it clear how matching values work, e.g. that we ignore additional filters when matching

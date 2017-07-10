Feature: Canonical URLs

  Scenario: Creating a value should expose canonical value URL
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "before"  | "string"     | "<"       | "ISO 8601"           |
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" when requested with:
      | /value |
      | 0.16   |
    Then "201 Created" was responded with headers:
      | Location                                                                         |
      | http://localhost:8080/keys/tax-rate/value?before=2007-01-01T00:00:00Z&country=DE |

  Scenario: Canonical value URL
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id      | /schema/type | /relation | /description |
      | "before" | "string"     | "<"       | "ISO 8601"   |
      | "income" | "number"     | "<="      | ".."         |
    And "PUT /keys/income-tax" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/income-tax/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/before     | /dimensions/income | /value |
      | "2018-01-01T00:00:00Z" | 256303             | 0.42   |
    Then "GET /keys/income-tax/value?before=2017-06-22T00:07:23Z&income=82000" responds "200 OK" with headers:
      | Content-Location                                                                      |
      | http://localhost:8080/keys/income-tax/value?before=2018-01-01T00:00:00Z&income=256303 |

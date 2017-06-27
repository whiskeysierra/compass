Feature: Value update

  Scenario: Updating values should allow to reorder
    Given "PUT /dimensions/country" returns successfully when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    When "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
    And "GET /keys/tax-rate/values" returns "200 OK" with a list of /values:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |

  Scenario: Updating values should return new values
    Given "PUT /dimensions/country" returns successfully when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    When "PUT /keys/tax-rate/values" when requested with a list of /values:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
    Then "200 OK" was returned with a list of /values:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |

  Scenario: Replacing values should create, update and delete
    Given "PUT /dimensions/{id}" (using /id) always returns "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "after"   | "string"     | ">="      | "ISO 8601"           |
    And "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /dimensions/country | /dimensions/after      | /value |
      | "AT"                |                        | 0.2    |
      | "CH"                |                        | 0.08   |
      | "DE"                |                        | 0.16   |
      | "DE"                | "2007-01-01T00:00:00Z" | 0.18   |
    When "PUT /keys/tax-rate/values" when requested with a list of /values:
      | /dimensions/country | /dimensions/after      | /value |
      | "CH"                |                        | 0.08   |
      | "DE"                |                        | 0.16   |
      | "DE"                | "2007-01-01T00:00:00Z" | 0.19   |
      | "FR"                |                        | 0.2    |
    Then "200 OK" was returned with a list of /values:
      | /dimensions/country | /dimensions/after      | /value |
      | "CH"                |                        | 0.08   |
      | "DE"                |                        | 0.16   |
      | "DE"                | "2007-01-01T00:00:00Z" | 0.19   |
      | "FR"                |                        | 0.2    |

  Scenario: Replacing values without dimensions
    Given "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /value |
      | 0.16   |
    When "PUT /keys/tax-rate/values" when requested with a list of /values:
      | /value |
      | 0.19   |
    Then "200 OK" was returned with a list of /values:
      | /value |
      | 0.19   |

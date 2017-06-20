Feature: Value update

  Scenario: Updating values should allow to reorder
    Given the following dimensions:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
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
    Given the following dimensions:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
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

  Scenario: Replacing values should delete
    Given the following dimensions:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    When "PUT /keys/tax-rate/values" when requested with a list of /values:
      | /dimensions/country | /value |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    Then "200 OK" was returned with a list of /values:
      | /dimensions/country | /value |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |

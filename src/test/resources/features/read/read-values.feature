Feature: Read values

  Background: Key
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |

  Scenario: Read values
    Given "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /value |
      | 0.19   |
    When "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /value |
      | 0.19   |

  Scenario: Last modified and ETag
    Given "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    Then  "GET /keys/tax-rate/values" responds "200 OK" with headers:
      | ETag          | Last-Modified                 |
      | "AAAAAAAAAAM" | Fri, 07 Jul 2017 22:09:21 GMT |

  Scenario: Read empty values
    Then "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"

  Scenario: Read values with dimensions
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
      | "FR"                | 0.2    |
    Then "GET /keys/tax-rate/values?country=DE" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |

  Scenario: Read values from from unknown key
    Then "GET /keys/unknown/values" responds "404 Not Found"

Feature: Value update

  Background: Values
    Given "PUT /dimensions/country" responds successfully when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |

  Scenario Outline: Partially updating value (JSON Merge Patch)
    When "PATCH /keys/tax-rate/value?country=DE" responds "200 OK" when requested with as "<content-type>":
      | /value |
      | 0.25   |
    And "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.25   |
    Examples:
      | content-type                 |
      | application/json             |
      | application/merge-patch+json |

  Scenario: Partially updating value (JSON Patch)
    When "PATCH /keys/tax-rate/value?country=DE" responds "200 OK" when requested with an array as "application/json-patch+json":
      | /op       | /path                 | /value |
      | "replace" | "/value"              | 0.25   |
    And "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.25   |

  Scenario Outline: Partially updating values
    When "PATCH /keys/tax-rate/values?country=DE" responds "200 OK" when requested with an array as "<content-type>":
      | /op       | /path                          | /value |
      | "replace" | "/values/2/value"              | 0.25   |
    And "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.25   |
    Examples:
      | content-type                |
      | application/json            |
      | application/json-patch+json |

  Scenario Outline: Partially updating values should allow to reorder
    When "PATCH /keys/tax-rate/values" responds "200 OK" when requested with an array as "<content-type>":
      | /op    | /from       | /path       |
      | "move" | "/values/2" | "/values/0" |
    And "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
    Examples:
      | content-type                |
      | application/json            |
      | application/json-patch+json |

  Scenario: Partially updating without match should fail
    When "PATCH /keys/tax-rate/value?country=DK" responds "404 Not Found" when requested with:
      | /value |
      | 0.25   |

Feature: /keys/{id}/values/revision/{revision}

  Background: Dimensions and key
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/feature.active" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "boolean"    | ".."         |

  Scenario: Read revision
    Given "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    And "PUT /keys/tax-rate/value?country=DE" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/values?country=DE" responds "204 No Content"
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |
    Then "GET /keys/tax-rate/values/revisions/4" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.16   |
    And "GET /keys/tax-rate/values/revisions/5" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
    And "GET /keys/tax-rate/values/revisions/6" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
    And "GET /keys/tax-rate/values/revisions/7" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |

  Scenario: Read revisions with filter
    Given "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
      | "FR"                | 0.2    |
    And "GET /keys/tax-rate/values/revisions/4?country=AT" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |

  Scenario: Read revision metadata
    Given "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    Then "GET /keys/tax-rate/values/revisions/4" responds "200 OK" with:
      | /revision/id | /revision/type | /revision/user | /revision/comment |
      | 4            | "update"       | "anonymous"    | ".."              |

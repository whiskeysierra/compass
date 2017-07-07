Feature: Value history

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

  Scenario: Updates values and read revisions
    When "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "CH"                | 0.08   |
      | "DE"                | 0.16   |
      | "AT"                | 0.2    |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |
    And "PUT /keys/feature.active/value" responds "201 Created" when requested with:
      | /value |
      | true   |
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 6   | "update" | "anonymous" | ".."     |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "update" | "anonymous" | ".."     |

  Scenario: Update value and read revisions
    Given "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    And "PUT /keys/tax-rate/value?country=DE" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/values?country=DE" responds "204 No Content"
    And "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/values?country=DE" responds "204 No Content"
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |
    And "PUT /keys/feature.active/value" responds "201 Created" when requested with:
      | /value |
      | true   |
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 9   | "update" | "anonymous" | ".."     |
      | 8   | "update" | "anonymous" | ".."     |
      | 7   | "update" | "anonymous" | ".."     |
      | 6   | "update" | "anonymous" | ".."     |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "update" | "anonymous" | ".."     |

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

  Scenario: Read value revisions without dimensions
    When "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    And "PUT /keys/tax-rate/value" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/values" responds "204 No Content"
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 6   | "update" | "anonymous" | ".."     |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "update" | "anonymous" | ".."     |

  Scenario: Read value revisions with dimensions
    Given "PUT /dimensions/after" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | "ISO 8601"   |
    When "PUT /keys/tax-rate/values?country=DE&after=2007-01-01T00:00:00Z" responds "201 Created" when requested with an array at "/values":
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 5   | "update" | "anonymous" | ".."     |

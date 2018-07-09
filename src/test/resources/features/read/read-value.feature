Feature: Read value

  Background: Dimensions and key
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description |
      | "before"  | "string"     | "<"       | "ISO 8601"   |
      | "country" | "string"     | "="       | "ISO 3166"   |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |

  Scenario: Read value with dimensions
    Given "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/before          | /dimensions/country | /value |
      |                             | "AT"                | 0.2    |
      |                             | "CH"                | 0.08   |
      | "2007-01-01T00:00:00+01:00" | "DE"                | 0.16   |
      |                             | "DE"                | 0.19   |
    Then "GET /keys/tax-rate/value?before=2006-06-22T00:07:23Z&country=DE" responds "200 OK" with:
      | /value |
      | 0.16   |

  Scenario: Last modified and ETag
    Given "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/value?country=DE" responds "200 OK" with headers:
      | ETag          | Last-Modified                 |
      | "AAAAAAAAAAQ" | Fri, 07 Jul 2017 22:09:21 GMT |

  Scenario: Read value without dimensions
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/value" responds "200 OK" with:
      | /value |
      | 0.19   |

  Scenario Outline: Read value for every relation
    Given "PUT /dimensions/before" responds "200 OK" when requested with:
      | /schema/type | /relation    | /description |
      | "string"     | "<relation>" | ".."         |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.19   |
      |                        | 0.16   |
    Then "GET /keys/tax-rate/value?before=<value>" responds "200 OK" with:
      | /value     |
      | <expected> |
    Examples:
      | relation | value                | expected |
      | <        | 2006-12-31T23:59:59Z | 0.19     |
      | <        | 2007-01-01T00:00:00Z | 0.16     |
      | <=       | 2006-12-31T23:59:59Z | 0.19     |
      | <=       | 2007-01-01T00:00:00Z | 0.19     |
      | <=       | 2007-01-01T00:00:01Z | 0.16     |
      | =        | 2006-12-31T23:59:59Z | 0.16     |
      | =        | 2007-01-01T00:00:00Z | 0.19     |
      | =        | 2007-01-01T00:00:01Z | 0.16     |
      | >=       | 2006-12-31T23:59:59Z | 0.16     |
      | >=       | 2007-01-01T00:00:00Z | 0.19     |
      | >=       | 2007-01-01T00:00:01Z | 0.19     |
      | >        | 2006-12-31T23:59:59Z | 0.16     |
      | >        | 2007-01-01T00:00:00Z | 0.16     |
      | >        | 2007-01-01T00:00:01Z | 0.19     |

  Scenario: Read fallback value
    Given "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
      |                     | 1      |
    Then "GET /keys/tax-rate/value" responds "200 OK" with:
      | /value |
      | 1      |

  Scenario: Read value from unknown key
    Then "GET /keys/unknown/value" responds "404 Not Found"

  Scenario: Read deleted value
    When "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/value?country=DE" responds "204 No Content"
    Then "GET /keys/tax-rate/value?country=DE" responds "404 Not Found"

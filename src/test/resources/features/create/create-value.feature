Feature: Value update

  Scenario: Creating a value without dimensions
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    When "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions | /value |
      | {}          | 0.19   |

  Scenario: Creating a value empty dimensions
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    When "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /dimensions | /value |
      | {}          | 0.19   |
    Then "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions | /value |
      | {}          | 0.19   |

  Scenario: Creating a value with dimensions
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "before"  | "string"     | "<"       | "ISO 8601"           |
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" responds "201 Created" when requested with:
      | /value |
      | 0.16   |

  Scenario: Creating a value with dimensions in body
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "before"  | "string"     | "<"       | "ISO 8601"           |
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" responds "201 Created" when requested with:
      | /dimension/country | /dimension/before      | /value |
      | "DE"               | "2007-01-01T00:00:00Z" | 0.16   |

  Scenario: Creating a value failed due to schema violation
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    When "PUT /keys/tax-rate/value" when requested with:
      | /value |
      | "19%"  |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /field   | /message                                |
      | "/value" | "/value: string found, number expected" |

  Scenario: Values and dimensions should support unions and null
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type/0 | /schema/type/1 | /relation | /description |
      | "string"       | "null"         | "="       | ".."         |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type/0 | /schema/type/1 | /description |
      | "number"       | "null"         | ".."         |
    When "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | null                | null   |
      |                     | 0      |
    Then "GET /keys/tax-rate/values" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | null                | null   |
      |                     | 0      |
    And "GET /keys/tax-rate/values?country=null" responds "200 OK" with an array at "/values":
      | /dimensions/country | /value |
      | null                | null   |
      |                     | 0      |

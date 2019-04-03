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

  Scenario: Creating a value succeeds when value doesn't exist
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    Then "PUT /keys/tax-rate/value" and "If-None-Match: *" responds "201 Created" when requested with:
      | /value |
      | 0.19   |

  Scenario: Creating a value fails when value already exists
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    And "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    Then "PUT /keys/tax-rate/value" and "If-None-Match: *" responds "412 Precondition Failed" when requested with:
      | /value |
      | 0.19   |

  Scenario: Creating values succeeds when values don't exist
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type/0 | /schema/type/1 | /relation | /description |
      | "string"       | "null"         | "="       | ".."         |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    Then "PUT /keys/tax-rate/values" and "If-None-Match: *" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "CH"                | 0.08   |

  Scenario: Creating values fails when values already exist
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type/0 | /schema/type/1 | /relation | /description |
      | "string"       | "null"         | "="       | ".."         |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    And "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
    Then "PUT /keys/tax-rate/values" and "If-None-Match: *" responds "412 Precondition Failed" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
      | "CH"                | 0.08   |

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
      | /dimensions/country | /dimensions/before     | /value |
      | "DE"                | "2007-01-01T00:00:00Z" | 0.16   |

  Scenario: Creating a value failed due to schema violation
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "GET /keys/tax-rate/values" responds "200 OK" with an empty array at "/values"
    When "PUT /keys/tax-rate/value" when requested with:
      | /value |
      | "19%"  |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /message                                                                                       |
      | "instance type (string) does not match any allowed primitive type (allowed: [integer,number])" |

  Scenario: Creating a value failed due to absent key
    Then "PUT /keys/tax-rate/value" responds "404 Not Found" when requested with:
      | /value |
      | 0.19   |

  Scenario: Creating a value failed due to absent dimension
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate/value?country=DE" when requested with:
      | /value |
      | 0.19   |
    Then "404 Not Found" was responded with:
      | /detail                 |
      | "Dimensions: [country]" |

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

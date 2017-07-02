Feature: Dimension creation

  Scenario: Creating a new dimension
    Given "GET /dimensions/example" responds "404 Not Found"
    When "PUT /dimensions/example" when requested with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    Then "201 Created" was responded with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    And "GET /dimensions/example" responds "200 OK" with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension without id property
    Given "GET /dimensions/example" responds "404 Not Found"
    When "PUT /dimensions/example" responds "201 Created" when requested with:
      | /schema/type | /relation | /description                 |
      | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/example" responds "200 OK" with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to id mismatch
    Given "GET /dimensions/example" responds "404 Not Found"
    When "PUT /dimensions/foo" responds "400 Bad Request" when requested with:
      | /id   | /schema/type | /relation | /description                 |
      | "bar" | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to unknown relation
    Given "GET /dimensions" responds "200 OK" with an empty array at "/dimensions"
    When "PUT /dimensions/example" responds "400 Bad Request" when requested with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "?"       | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to schema violation
    When "PUT /dimensions/FOO" when requested with:
      | /schema/type | /relation | /description |
      | "any"        | 17        | false        |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /field          | /message                                                                                                          |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.id"          | "$.id: does not match the regex pattern ^([a-z0-9]+(-[a-z0-9]+)*)([.]([a-z0-9]+(-[a-z0-9]+)*))*$"                 |
      | "$.relation"    | "$.relation: integer found, string expected"                                                                      |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

  Scenario Outline: Creating a new dimension fails due to reserved keywords
    When "PUT /dimensions/<dimension>" when requested with:
      | /schema/type | /relation | /description                 |
      | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    Then "405 Method Not Allowed" was responded with:
      | /detail          |
      | "ID is reserved" |
    Examples:
      | dimension |
      | cursor    |
      | embed     |
      | fields    |
      | filter    |
      | key       |
      | limit     |
      | offset    |
      | q         |
      | query     |
      | revisions |
      | sort      |

  Scenario: Creating a new dimension fails due to unsupported schema type
    When "PUT /dimensions/example" when requested with:
      | /schema/type | /relation | /description |
      | "number"     | "~"       | ".."         |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /message                                       |
      | "[number] not among supported types: [string]" |

Feature: Dimension creation

  Scenario: Creating a new dimension
    Given "GET /dimensions/example" returns "404 Not Found"
    When "PUT /dimensions/example" returns "201 Created" when requested with:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/example" returns "200 OK" with:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension without id property
    Given "GET /dimensions/example" returns "404 Not Found"
    When "PUT /dimensions/example" returns "201 Created" when requested with:
      | schema.type | relation | description                  |
      | "string"    | "="      | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/example" returns "200 OK" with:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to id mismatch
    Given there are no dimensions
    When "PUT /dimensions/foo" returns "400 Bad Request" when requested with:
      | id    | schema.type | relation | description                  |
      | "bar" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to unknown relation
    Given there are no dimensions
    When "PUT /dimensions/example" returns "400 Bad Request" when requested with:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "?"      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to schema violation
    Given there are no dimensions
    When "PUT /dimensions/FOO" when requested with:
      | schema.type | relation | description |
      | "any"       | 17       | false       |
    Then "400 Bad Request" was returned with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.id"          | "$.id: does not match the regex pattern ^([a-z0-9]+(-[a-z0-9]+)*)([.]([a-z0-9]+(-[a-z0-9]+)*))*$"                 |
      | "$.relation"    | "$.relation: integer found, string expected"                                                                      |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

  Scenario Outline: Creating a new dimension fails due to reserved keywords
    Given there are no dimensions
    When "PUT /dimensions/<dimension>" when requested with:
      | schema.type | relation | description                  |
      | "string"    | "="      | "Lorem ipsum dolor sit amet" |
    Then "400 Bad Request" was returned with a list of violations:
      | message                         |
      | "may not be a reserved keyword" |
    Examples:
      | dimension    |
      | cursor       |
      | embed        |
      | fields       |
      | filter       |
      | key          |
      | limit        |
      | offset       |
      | q            |
      | query        |
      | sort         |

  Scenario: Creating a new dimension fails due to unsupported schema type
    Given there are no dimensions
    When "PUT /dimensions/example" when requested with:
      | schema.type | relation | description |
      | "number"    | "~"      | ".."        |
    Then "400 Bad Request" was returned with a list of violations:
      | message                                           |
      | "'number' is not among supported types: [string]" |

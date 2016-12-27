Feature: Dimension creation

  Scenario: Creating a new dimension
    Given "GET /dimensions/example" returns "404 Not Found"
    When "PUT /dimensions/example" is requested with this it returns "201 Created":
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/example" returns:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to ID mismatch
    Given there are no dimensions
    When "PUT /dimensions/foo" is requested with this it returns "400 Bad Request":
      | id    | schema.type | relation | description                  |
      | "bar" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to schema violation
    Given there are no dimensions
    When "PUT /dimensions/foo" is requested with this:
      | id    | schema.type | relation | description |
      | "foo" | "any"       | 17       | false       |
    Then it returns "400 Bad Request" with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.relation"    | "$.relation: integer found, string expected"                                                                      |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

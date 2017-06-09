Feature: Dimension creation

  Scenario: Creating a new dimension
    Given "GET /dimensions/example" returns "404 Not Found"
    When "PUT /dimensions/example" returns "201 Created" when requested with:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/example" returns "200 OK" with:
      | id        | schema.type | relation | description                  |
      | "example" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  # TODO dimension.id is not required

  Scenario: Creating a new dimension failed due to ID mismatch
    Given there are no dimensions
    When "PUT /dimensions/foo" returns "400 Bad Request" when requested with:
      | id    | schema.type | relation | description                  |
      | "bar" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension failed due to schema violation
    Given there are no dimensions
    When "PUT /dimensions/foo" when requested with:
      | schema.type | relation | description |
      | "any"       | 17       | false       |
    Then "400 Bad Request" was returned with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
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
      | order_by     |
      | page_size    |
      | page_token   |
      | q            |
      | query        |
      | show_deleted |
      | sort         |

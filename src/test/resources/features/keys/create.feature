Feature: Key creation

  Scenario: Creating a new key
    Given "GET /keys/example" returns "404 Not Found"
    When "PUT /keys/example" is requested with this it returns "201 Created":
      | id        | schema.type | description                  |
      | "example" | "string"    | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/example" returns:
      | id        | schema.type | description                  |
      | "example" | "string"    | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key failed due to ID mismatch
    Given there are no keys
    When "PUT /keys/foo" is requested with this it returns "400 Bad Request":
      | id    | schema.type | description                  |
      | "bar" | "string"    | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key failed due to schema violation
    Given there are no keys
    When "PUT /keys/foo" is requested with this:
      | id    | schema.type | description |
      | "foo" | "any"       | false       |
    Then it returns "400 Bad Request" with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

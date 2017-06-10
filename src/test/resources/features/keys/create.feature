Feature: Key creation

  Scenario: Creating a new key
    Given "GET /keys/example" returns "404 Not Found"
    When "PUT /keys/example" returns "201 Created" when requested with:
      | id        | schema.type | description                  |
      | "example" | "string"    | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/example" returns "200 OK" with:
      | id        | schema.type | description                  |
      | "example" | "string"    | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key failed due to id mismatch
    Given there are no keys
    When "PUT /keys/foo" returns "400 Bad Request" when requested with:
      | id    | schema.type | description                  |
      | "bar" | "string"    | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key failed due to schema violation
    Given there are no keys
    When "PUT /keys/foo" when requested with:
      | id    | schema.type | description |
      | "foo" | "any"       | false       |
    Then "400 Bad Request" was returned with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

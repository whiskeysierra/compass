  Feature: Key creation

  Scenario: Creating a new key
    Given "GET /keys/example" responds "404 Not Found"
    When "PUT /keys/example" when requested with:
      | /id       | /schema/type | /description                 |
      | "example" | "string"     | "Lorem ipsum dolor sit amet" |
    Then "201 Created" was responded with:
      | /id       | /schema/type | /description                 |
      | "example" | "string"     | "Lorem ipsum dolor sit amet" |
    And "GET /keys/example" responds "200 OK" with:
      | /id       | /schema/type | /description                 |
      | "example" | "string"     | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key failed due to id mismatch
    Given "GET /keys/foo" responds "404 Not Found"
    Then "PUT /keys/foo" responds "400 Bad Request" when requested with:
      | /id   | /schema/type | /description                 |
      | "bar" | "string"     | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key failed due to schema violation
    When "PUT /keys/FOO" when requested with:
      | /schema/type | /description |
      | "any"        | false        |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /field          | /message                                                                                                          |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.id"          | "$.id: does not match the regex pattern ^([a-z0-9]+(-[a-z0-9]+)*)([.]([a-z0-9]+(-[a-z0-9]+)*))*$"                 |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

    Scenario Outline: Creating a new key fails due to reserved keywords
      When "PUT /keys/<key>" when requested with:
        | /schema/type | /description                 |
        | "string"     | "Lorem ipsum dolor sit amet" |
      Then "400 Bad Request" was responded with an array at "/violations":
        | /message                        |
        | "may not be a reserved keyword" |
      Examples:
        | key       |
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

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

  Scenario: Creating a new key succeeds when key doesn't exist
    Given "GET /keys/example" responds "404 Not Found"
    Then "PUT /keys/example" and "If-None-Match: *" responds "201 Created" when requested with:
      | /id       | /schema/type | /description                 |
      | "example" | "string"     | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new key fails when key already exists
    Given "PUT /keys/example" responds "201 Created" when requested with:
      | /id       | /schema/type | /description                 |
      | "example" | "string"     | "Lorem ipsum dolor sit amet" |
    Then "PUT /keys/example" and "If-None-Match: *" responds "412 Precondition Failed" when requested with:
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
      | /field         | /message                                                                                         |
      | "/description" | "/description: boolean found, string expected"                                                   |
      | "/id"          | "/id: does not match the regex pattern ^([a-z0-9]+(-[a-z0-9]+)*)([.]([a-z0-9]+(-[a-z0-9]+)*))*$" |
      | "/schema/type" | "/schema/type: should be valid to any of the schemas [array]"                                    |

  Scenario Outline: Creating a new key fails due to reserved keywords
    When "PUT /keys/<key>" when requested with:
      | /schema/type | /description                 |
      | "string"     | "Lorem ipsum dolor sit amet" |
    Then "405 Method Not Allowed" was responded with:
      | /detail          |
      | "ID is reserved" |
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

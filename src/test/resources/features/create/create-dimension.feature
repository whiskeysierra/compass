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

  Scenario: Creating a new dimension succeeds when dimension doesn't exist
    Given "GET /dimensions/example" responds "404 Not Found"
    Then "PUT /dimensions/example" and "If-None-Match: *" responds "201 Created" when requested with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Creating a new dimension fails when dimension already exists
    Given "PUT /dimensions/example" responds "201 Created" when requested with:
      | /id       | /schema/type | /relation | /description                 |
      | "example" | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    Then "PUT /dimensions/example" and "If-None-Match: *" responds "412 Precondition Failed" when requested with:
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
      | /message                                                                                                      |
      | "ECMA 262 regex ^([a-z0-9]+(-[a-z0-9]+)*)([.]([a-z0-9]+(-[a-z0-9]+)*))*$ does not match input string FOO"     |
      | "[Path '/description'] Instance type (boolean) does not match any allowed primitive type (allowed: [string])" |
      | "[Path '/relation'] Instance type (integer) does not match any allowed primitive type (allowed: [string])"    |
      | "[Path '/schema/type'] Instance failed to match at least one required schema among 2"                         |

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

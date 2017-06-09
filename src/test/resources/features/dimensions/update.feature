Feature: Dimension update

  Scenario: Updating a dimension
    Given the following dimensions:
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |
    When "PUT /dimensions/device" returns "200 OK" when requested with:
      | id       | schema.type | schema.enum          | relation | description                  |
      | "device" | "string"    | ["mobile","desktop"] | "="      | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/device" returns "200 OK" with:
      | id       | schema.type | schema.enum          | relation | description                  |
      | "device" | "string"    | ["mobile","desktop"] | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a dimension failed due to ID mismatch
    Given the following dimensions:
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |
    When "PUT /dimensions/device" returns "400 Bad Request" when requested with:
      | id    | schema.type | relation | description                  |
      | "bar" | "string"    | "="      | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a dimension failed due to schema violation
    Given the following dimensions:
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |
    When "PUT /dimensions/device" when requested with:
      | id       | schema.type | relation | description |
      | "device" | "any"       | 17       | false       |
    Then "400 Bad Request" was returned with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.relation"    | "$.relation: integer found, string expected"                                                                      |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

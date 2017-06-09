Feature: Reading dimensions

  Scenario: List dimensions
    Given the following dimensions:
      | id         | schema                                 | relation | description |
      | "device"   | {"type":"string"}                      | "="      | ".."        |
      | "language" | {"type":"string","format":"bcp47"}     | "^"      | ".."        |
      | "location" | {"type":"string","format":"geohash"}   | "^"      | ".."        |
      | "before"   | {"type":"string","format":"date-time"} | "<="     | ".."        |
      | "after"    | {"type":"string","format":"date-time"} | ">="     | ".."        |
      | "email"    | {"type":"string","format":"email"}     | "~"      | ".."        |
    When "GET /dimensions" returns "200 OK" with a list of dimensions:
      | id         | schema                                 | relation | description |
      | "after"    | {"type":"string","format":"date-time"} | ">="     | ".."        |
      | "before"   | {"type":"string","format":"date-time"} | "<="     | ".."        |
      | "device"   | {"type":"string"}                      | "="      | ".."        |
      | "email"    | {"type":"string","format":"email"}     | "~"      | ".."        |
      | "language" | {"type":"string","format":"bcp47"}     | "^"      | ".."        |
      | "location" | {"type":"string","format":"geohash"}   | "^"      | ".."        |

  Scenario: List empty dimensions
    Given there are no dimensions
    Then "GET /dimensions" returns "200 OK" with an empty list of dimensions

  Scenario: Get dimension
    Given "PUT /dimensions/device" returns "201 Created" when requested with:
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |
    When "GET /dimensions/device" returns "200 OK" with:
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |

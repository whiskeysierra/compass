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
    When "GET /dimensions" returns a list of dimensions:
      | id         | schema                                 | relation | description |
      | "after"    | {"type":"string","format":"date-time"} | ">="     | ".."        |
      | "before"   | {"type":"string","format":"date-time"} | "<="     | ".."        |
      | "device"   | {"type":"string"}                      | "="      | ".."        |
      | "email"    | {"type":"string","format":"email"}     | "~"      | ".."        |
      | "language" | {"type":"string","format":"bcp47"}     | "^"      | ".."        |
      | "location" | {"type":"string","format":"geohash"}   | "^"      | ".."        |

  Scenario: List empty dimensions
    Given there are no dimensions
    Then "GET /dimensions" returns an empty list of dimensions

  Scenario: Get dimension
    Given "PUT /dimensions/device" is requested with this it returns "201 Created":
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |
    When "GET /dimensions/device" returns:
      | id       | schema.type | relation | description |
      | "device" | "string"    | "="      | ".."        |

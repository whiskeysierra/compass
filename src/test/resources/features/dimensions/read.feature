Feature: Dimensions

  Scenario: List dimensions
    Given the default dimensions
    When "GET /dimensions" returns a list of dimensions:
      | id         | schema                                 | relation | description |
      | "device"   | {"type":"string"}                      | "="      | ".."        |
      | "language" | {"type":"string","format":"bcp47"}     | "^"      | ".."        |
      | "location" | {"type":"string","format":"geohash"}   | "^"      | ".."        |
      | "before"   | {"type":"string","format":"date-time"} | "<="     | ".."        |
      | "after"    | {"type":"string","format":"date-time"} | ">="     | ".."        |
      | "email"    | {"type":"string","format":"email"}     | "~"      | ".."        |

  Scenario: List empty dimensions
    Given there are no dimensions
    Then "GET /dimensions" returns an empty list of dimensions

  Scenario: Get dimension
    Given the default dimensions
    When "GET /dimensions/device" returns:
      | id       | schema            | relation | description |
      | "device" | {"type":"string"} | "="      | ".."        |

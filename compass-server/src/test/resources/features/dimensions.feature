Feature: Dimensions

  Scenario: List dimensions
    Given the default dimensions
    When "GET /dimensions" is requested
    Then the following dimensions are returned:
      | ID       | Schema                                 | Relation | Description |
      | device   | {"type":"string"}                      | =        |             |
      | language | {"type":"string","format":"bcp47"}     | ^        |             |
      | location | {"type":"string","format":"geohash"    | ^        |             |
      | before   | {"type":"string","format":"date-time"} | <=       |             |
      | after    | {"type":"string","format":"date-time"} | >=       |             |
      | email    | {"type":"string","format":"email"}     | ~        |             |

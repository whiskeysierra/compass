Feature: Dimensions

  Scenario: List dimensions
    Given the default dimensions
    When "GET /dimensions" is requested
    Then the following dimensions are returned:
      | id       | schema                                 | relation | description |
      | device   | {"type":"string"}                      | =        | ..          |
      | language | {"type":"string","format":"bcp47"}     | ^        | ..          |
      | location | {"type":"string","format":"geohash"}   | ^        | ..          |
      | before   | {"type":"string","format":"date-time"} | <=       | ..          |
      | after    | {"type":"string","format":"date-time"} | >=       | ..          |
      | email    | {"type":"string","format":"email"}     | ~        | ..          |

  Scenario: List empty dimensions
    Given there are no dimensions
    When "GET /dimensions" is requested
    Then no dimensions are returned:

  Scenario: Get dimension
    Given the default dimensions
    When "GET /dimensions/device" is requested
    Then the following is returned:
      | id     | schema            | relation | description |
      | device | {"type":"string"} | =        | ..          |

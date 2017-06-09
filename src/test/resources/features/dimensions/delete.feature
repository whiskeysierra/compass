Feature: Dimension deletion

  Scenario: Delete dimension
    Given the following dimensions:
      | id         | schema                               | relation | description |
      | "device"   | {"type":"string"}                    | "="      | ".."        |
      | "language" | {"type":"string","format":"bcp47"}   | "^"      | ".."        |
      | "location" | {"type":"string","format":"geohash"} | "^"      | ".."        |
    When "DELETE /dimensions/device" returns "204 No Content"
    When "GET /dimensions" returns "200 OK" with a list of dimensions:
      | id         | schema                               | relation | description |
      | "language" | {"type":"string","format":"bcp47"}   | "^"      | ".."        |
      | "location" | {"type":"string","format":"geohash"} | "^"      | ".."        |

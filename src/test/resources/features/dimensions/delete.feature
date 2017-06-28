Feature: Dimension deletion

  Scenario: Delete dimension
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "device"   | "string"     |                | "="       | ".."         |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |
    When "DELETE /dimensions/device" responds "204 No Content"
    When "GET /dimensions" responds "200 OK" with an array at "/dimensions":
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |

  Scenario: Deleting unknown dimension fails
    Given "GET /dimensions/example" responds "404 Not Found"
    Then "DELETE /dimensions/example" responds "404 Not Found"

  Scenario: Delete used dimension
    Given "PUT /dimensions/before" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "<="      | "ISO 8601"   |
    Given "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.19   |
      |                        | 0.16   |
    Then "DELETE /dimensions/before" responds "400 Bad Request"

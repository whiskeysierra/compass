Feature: Dimension deletion

  Scenario: Delete dimension
    Given "PUT /dimensions/{id}" (using /id) always returns "201 Created" when requested individually with:
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "device"   | "string"     |                | "="       | ".."         |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |
    When "DELETE /dimensions/device" returns "204 No Content"
    When "GET /dimensions" returns "200 OK" with a list of /dimensions:
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |

  Scenario: Deleting unknown dimension fails
    Given "GET /dimensions/example" returns "404 Not Found"
    Then "DELETE /dimensions/example" returns "404 Not Found"

  Scenario: Delete used dimension
    Given "PUT /dimensions/before" returns successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "<="      | "ISO 8601"   |
    Given "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.19   |
      |                        | 0.16   |
    Then "DELETE /dimensions/before" returns "400 Bad Request"

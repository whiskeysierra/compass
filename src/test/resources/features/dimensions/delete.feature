Feature: Dimension deletion

  Scenario: Delete dimension
    Given the following dimensions:
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
    Given there are no dimensions
    Then "DELETE /dimensions/example" returns "404 Not Found"

  Scenario: Delete used dimension
    Given the following dimensions:
      | /id      | /schema/type | /relation | /description |
      | "before" | "string"     | "<="      | "ISO 8601"   |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.19   |
      |                        | 0.16   |
    Then "DELETE /dimensions/before" returns "400 Bad Request"

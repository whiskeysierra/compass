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

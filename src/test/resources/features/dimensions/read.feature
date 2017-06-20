Feature: Reading dimensions

  Scenario: List dimensions
    Given the following dimensions:
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "device"   | "string"     |                | "="       | ".."         |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |
      | "before"   | "string"     | "date-time"    | "<="      | ".."         |
      | "after"    | "string"     | "date-time"    | ">="      | ".."         |
      | "email"    | "string"     | "email"        | "~"       | ".."         |
    When "GET /dimensions" returns "200 OK" with a list of /dimensions:
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "after"    | "string"     | "date-time"    | ">="      | ".."         |
      | "before"   | "string"     | "date-time"    | "<="      | ".."         |
      | "device"   | "string"     |                | "="       | ".."         |
      | "email"    | "string"     | "email"        | "~"       | ".."         |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |

  Scenario: List empty dimensions
    Given there are no dimensions
    Then "GET /dimensions" returns "200 OK" with an empty list of /dimensions

  Scenario: Get dimension
    Given "PUT /dimensions/device" returns "201 Created" when requested with:
      | /id      | /schema/type | /relation | /description |
      | "device" | "string"     | "="       | ".."         |
    When "GET /dimensions/device" returns "200 OK" with:
      | /id      | /schema/type | /relation | /description |
      | "device" | "string"     | "="       | ".."         |

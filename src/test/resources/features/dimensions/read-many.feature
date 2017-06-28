Feature: Reading dimensions

  Scenario: List dimensions
    Given "PUT /dimensions/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "device"   | "string"     |                | "="       | ".."         |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |
      | "before"   | "string"     | "date-time"    | "<="      | ".."         |
      | "after"    | "string"     | "date-time"    | ">="      | ".."         |
      | "email"    | "string"     | "email"        | "~"       | ".."         |
    When "GET /dimensions" responds "200 OK" with an array at "/dimensions":
      | /id        | /schema/type | /schema/format | /relation | /description |
      | "after"    | "string"     | "date-time"    | ">="      | ".."         |
      | "before"   | "string"     | "date-time"    | "<="      | ".."         |
      | "device"   | "string"     |                | "="       | ".."         |
      | "email"    | "string"     | "email"        | "~"       | ".."         |
      | "language" | "string"     | "bcp47"        | "^"       | ".."         |
      | "location" | "string"     | "geohash"      | "^"       | ".."         |

  Scenario: List empty dimensions
    Then "GET /dimensions" responds "200 OK" with an empty array at "/dimensions"

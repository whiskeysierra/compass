Feature: Read keys

  Scenario: Read keys
    Given "PUT /keys/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    Then "GET /keys" responds "200 OK" with an array at "/keys":
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |

  Scenario: Read empty keys
    Then "GET /keys" responds "200 OK" with an empty array at "/keys"

Feature: Reading keys

  Scenario: List keys
    Given "PUT /keys/{id}" (using /id) always returns "201 Created" when requested individually with:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    Then "GET /keys" returns "200 OK" with a list of /keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |

  Scenario: List empty keys
    Then "GET /keys" returns "200 OK" with an empty list of /keys

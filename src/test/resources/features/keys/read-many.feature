Feature: Reading keys

  Scenario: List keys
    Given the following keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    Then "GET /keys" returns "200 OK" with a list of /keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |

  Scenario: List empty keys
    Given there are no keys
    Then "GET /keys" returns "200 OK" with an empty list of /keys

  Scenario: List keys by key pattern:
    Given the following keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    Then "GET /keys?q=tax" returns "200 OK" with a list of /keys:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |
    And "GET /keys?q=feature" returns "200 OK" with a list of /keys:
      | /id              | /schema/type | /description |
      | "feature.active" | "boolean"    | ".."         |
    And "GET /keys?q=at" returns "200 OK" with a list of /keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |

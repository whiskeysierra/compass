Feature: Reading keys

  Scenario: List keys
    Given the following keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |
    Then "GET /keys" returns "200 OK" with a list of keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |

  Scenario: List empty keys
    Given there are no keys
    Then "GET /keys" returns "200 OK" with an empty list of keys

  Scenario: List keys by key pattern:
    Given the following keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |
    Then "GET /keys?q=tax" returns "200 OK" with a list of keys:
      | id               | schema                              | description |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |
    And "GET /keys?q=feature" returns "200 OK" with a list of keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
    And "GET /keys?q=at" returns "200 OK" with a list of keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |

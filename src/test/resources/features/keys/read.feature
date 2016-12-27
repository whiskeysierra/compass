Feature: Reading keys

  Scenario: List keys
    Given the following keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |
    When "GET /keys" returns a list of keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |

  Scenario: List empty keys
    Given there are no keys
    Then "GET /keys" returns an empty list of keys

  Scenario: Get key
    Given the following keys:
      | id         | schema.type | schema.format | description |
      | "tax-rate" | "number"    | "double"      | ".."        |
    When "GET /keys/tax-rate" returns:
      | id         | schema.type | schema.format | description |
      | "tax-rate" | "number"    | "double"      | ".."        |

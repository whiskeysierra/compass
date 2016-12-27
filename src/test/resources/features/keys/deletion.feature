Feature: Key deletion

  Scenario: Delete key
    Given the following keys:
      | id               | schema                              | description |
      | "feature.active" | {"type":"boolean"}                  | ".."        |
      | "tax-rate"       | {"type":"number","format":"double"} | ".."        |
    When "DELETE /keys/feature.active" returns "204 No Content"
    When "GET /keys" returns a list of keys:
      | id         | schema                              | description |
      | "tax-rate" | {"type":"number","format":"double"} | ".."        |

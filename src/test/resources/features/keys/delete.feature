Feature: Key deletion

  Scenario: Delete key
    Given the following keys:
      | /id              | /schema/type | /schema/format | /description |
      | "feature.active" | "boolean"    |                | ".."         |
      | "tax-rate"       | "number"     | "double"       | ".."         |
    When "DELETE /keys/feature.active" returns "204 No Content"
    When "GET /keys" returns "200 OK" with a list of /keys:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |

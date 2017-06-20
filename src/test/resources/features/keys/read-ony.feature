Feature: Reading keys

  Scenario: Get key
    Given the following keys:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |
    When "GET /keys/tax-rate" returns "200 OK" with:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |

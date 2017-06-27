Feature: Reading keys

  Scenario: Get key
    Given "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /schema/format | /description |
      | "number"     | "double"       | ".."         |
    When "GET /keys/tax-rate" returns "200 OK" with:
      | /id        | /schema/type | /schema/format | /description |
      | "tax-rate" | "number"     | "double"       | ".."         |
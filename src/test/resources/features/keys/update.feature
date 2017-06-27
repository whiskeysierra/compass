Feature: Key update

  Scenario: Updating a key
    Given "PUT /keys/feature.active" returns successfully when requested with:
      | /schema/type | /description |
      | "boolean"    | ".."         |
    When "PUT /keys/feature.active" when requested with:
      | /id              | /schema/type | /schema/enum         | /description                 |
      | "feature.active" | "string"     | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |
    Then "200 OK" was returned with:
      | /id              | /schema/type | /schema/enum         | /description                 |
      | "feature.active" | "string"     | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |
    And "GET /keys/feature.active" returns "200 OK" with:
      | /id              | /schema/type | /schema/enum         | /description                 |
      | "feature.active" | "string"     | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |

  Scenario: Update description alone
    Given "PUT /keys/feature.active" returns successfully when requested with:
      | /schema/type | /description |
      | "boolean"    | ".."         |
    When "PUT /keys/feature.active" returns "200 OK" when requested with:
      | /id              | /schema/type | /description                 |
      | "feature.active" | "boolean"    | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/feature.active" returns "200 OK" with:
      | /id              | /schema/type | /description                 |
      | "feature.active" | "boolean"    | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a key failed due to id mismatch
    Given "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate" returns "400 Bad Request" when requested with:
      | /id   | /schema/type | /description                 |
      | "bar" | "number"     | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a key failed due to schema violation
    Given "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate" when requested with:
      | /id        | /schema/type | /description |
      | "tax-rate" | "any"        | false        |
    Then "400 Bad Request" was returned with a list of /violations:
      | /field          | /message                                                                                                          |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

  Scenario: Updating a dimension's schema should fail if at least one value violates it
    Given "PUT /dimensions/country" returns successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
      | /dimensions/country | /value |
      | "AT"                | 20     |
      | "CH"                | 8      |
      | "DE"                | 19     |
    When "PUT /keys/tax-rate" when requested with:
      | /schema/type | /schema/minimum | /schema/maximum | /description |
      | "number"     | 0.0             | 1.0             | ".."         |
    Then "400 Bad Request" was returned with a list of /violations:
      | /message                                    |
      | "$.value: must have a maximum value of 1.0" |
      | "$.value: must have a maximum value of 1.0" |
      | "$.value: must have a maximum value of 1.0" |

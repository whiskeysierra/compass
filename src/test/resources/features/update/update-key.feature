Feature: Key update

  Scenario: Updating a key
    Given "PUT /keys/feature.active" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "boolean"    | ".."         |
    When "PUT /keys/feature.active" when requested with:
      | /id              | /schema/type | /schema/enum         | /description                 |
      | "feature.active" | "string"     | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |
    Then "200 OK" was responded with:
      | /id              | /schema/type | /schema/enum         | /description                 |
      | "feature.active" | "string"     | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |
    And "GET /keys/feature.active" responds "200 OK" with:
      | /id              | /schema/type | /schema/enum         | /description                 |
      | "feature.active" | "string"     | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |

  Scenario: Update description alone
    Given "PUT /keys/feature.active" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "boolean"    | ".."         |
    When "PUT /keys/feature.active" responds "200 OK" when requested with:
      | /id              | /schema/type | /description                 |
      | "feature.active" | "boolean"    | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/feature.active" responds "200 OK" with:
      | /id              | /schema/type | /description                 |
      | "feature.active" | "boolean"    | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a key with values
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    Then "PATCH /keys/tax-rate" responds "200 OK" when requested with:
      | /schema/minimum | /schema/maximum |
      | 0.0             | 1.0             |

  Scenario: Updating a key failed due to schema violation
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "PUT /keys/tax-rate" when requested with:
      | /id        | /schema/type | /description |
      | "tax-rate" | "any"        | false        |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /message                                                                                                      |
      | "[Path '/description'] Instance type (boolean) does not match any allowed primitive type (allowed: [string])" |
      | "[Path '/schema/type'] Instance failed to match at least one required schema among 2"                         |

  Scenario: Updating a dimension's schema should fail if at least one value violates it
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 20     |
      | "CH"                | 8      |
      | "DE"                | 19     |
    When "PUT /keys/tax-rate" when requested with:
      | /schema/type | /schema/minimum | /schema/maximum | /description |
      | "number"     | 0.0             | 1.0             | ".."         |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /message                                                                        |
      | "numeric instance is greater than the required maximum (maximum: 1, found: 20)" |
      | "numeric instance is greater than the required maximum (maximum: 1, found: 8)"  |
      | "numeric instance is greater than the required maximum (maximum: 1, found: 19)" |

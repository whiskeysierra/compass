Feature: Dimension update

  Scenario: Updating a dimension
    Given "PUT /dimensions/version" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "PUT /dimensions/version" when requested with:
      | /id       | /schema/type | /relation | /description                 |
      | "version" | "number"     | ">"       | "Lorem ipsum dolor sit amet" |
    Then "200 OK" was responded with:
      | /id       | /schema/type | /relation | /description                 |
      | "version" | "number"     | ">"       | "Lorem ipsum dolor sit amet" |
    And "GET /dimensions/version" responds "200 OK" with:
      | /id       | /schema/type | /relation | /description                 |
      | "version" | "number"     | ">"       | "Lorem ipsum dolor sit amet" |

  Scenario: Update description alone
    Given "PUT /dimensions/version" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "PUT /dimensions/version" responds "200 OK" when requested with:
      | /id       | /schema/type | /relation | /description                 |
      | "version" | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/version" responds "200 OK" with:
      | /id       | /schema/type | /relation | /description                 |
      | "version" | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a dimension failed due to id mismatch
    Given "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "PUT /dimensions/device" responds "400 Bad Request" when requested with:
      | /id   | /schema/type | /relation | /description                 |
      | "bar" | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a dimension failed due to schema violation
    Given "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "PUT /dimensions/device" when requested with:
      | /id      | /schema/type | /relation | /description |
      | "device" | "any"        | 17        | false        |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /field          | /message                                                                                                          |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.relation"    | "$.relation: integer found, string expected"                                                                      |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

  Scenario: Update dimension with values
    Given "PUT /dimensions/country" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "<="      | "ISO 8601"   |
    And "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    Then "PUT /dimensions/country" responds "200 OK" when requested with:
      | /schema/type | /schema/pattern | /relation | /description         |
      | "string"     | "[A-Z]{2}"      | "="       | "ISO 3166-1 alpha-2" |

  Scenario: Updating a dimension's schema should fail if at least one value violates it
    Given "PUT /dimensions/country" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "<="      | "ISO 8601"   |
    And "PUT /keys/tax-rate" responds successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" responds "200 OK" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    When "PUT /dimensions/country" when requested with:
      | /schema/type | /schema/pattern | /relation | /description |
      | "string"     | "[a-z]{2}"      | "="       | ".."         |
    Then "400 Bad Request" was responded with an array at "/violations":
      | /message                                                          |
      | "$.dimensions.country: does not match the regex pattern [a-z]{2}" |
      | "$.dimensions.country: does not match the regex pattern [a-z]{2}" |
      | "$.dimensions.country: does not match the regex pattern [a-z]{2}" |

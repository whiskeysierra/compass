Feature: Reading values

  Scenario: List empty values
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And there are no values
    Then "GET /keys/tax-rate/values" returns an empty list of values

  Scenario: List values
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | value |
      | 0.19  |
    When "GET /keys/tax-rate/values" returns a list of values:
      | value |
      | 0.19  |

  Scenario: List values with dimensions
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | dimensions.country | value |
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |
    When "GET /keys/tax-rate/values?country=DE" returns a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |

  Scenario: Get value
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | value |
      | 0.19  |
    When "GET /keys/tax-rate/value?country=DE" returns:
      | value |
      | 0.19  |

  Scenario: Get value with dimension
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | dimensions.country | value |
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |
    When "GET /keys/tax-rate/value?country=DE" returns:
      | dimensions.country | value |
      | "DE"               | 0.19  |

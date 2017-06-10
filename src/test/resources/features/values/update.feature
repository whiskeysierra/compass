Feature: Value update

  # TODO verify dimensions in body are optional
  # TODO verify dimensions in body have to match query if present

  Scenario: Updating a value without dimensions
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "PUT /keys/tax-rate/value" returns "201 Created" when requested with:
      | value |
      | 0.16  |
    Then "PUT /keys/tax-rate/value" returns "200 OK" when requested with:
      | value |
      | 0.19  |

  Scenario: Updating a value with dimensions
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
      | "before"  | "string"    | "<"      | "ISO 8601"           |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "201 Created" when requested with:
      | value |
      | 0.16  |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "200 OK" when requested with:
      | value |
      | 0.16  |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | value |
      | 0.16  |

  Scenario: Reorder values
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And the following values for key tax-rate:
      | dimensions.country | value |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |
    When "PUT /keys/tax-rate/values" when requested with a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |
    Then "200 OK" was returned with a list of values:
    # TODO move this to its own scenario
      | dimensions.country | value |
      | "DE"               | 0.19  |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |
    And "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |

  # TODO verify that PUT actually deletes values

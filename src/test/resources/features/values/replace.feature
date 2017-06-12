Feature: Value update

  Scenario: Creating a value without dimensions
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "PUT /keys/tax-rate/value" returns "201 Created" when requested with:
      | value |
      | 0.19  |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions | value |
      | {}         | 0.19  |

  Scenario: Creating a value empty dimensions
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "PUT /keys/tax-rate/value" returns "201 Created" when requested with:
      | dimensions | value |
      | {}         | 0.19  |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions | value |
      | {}         | 0.19  |

  Scenario: Replacing a value with dimensions
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
      | "before"  | "string"    | "<"      | "ISO 8601"           |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "201 Created" when requested with:
      | value |
      | 0.16  |

  Scenario: Replacing a value with dimensions in body
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
      | "before"  | "string"    | "<"      | "ISO 8601"           |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "201 Created" when requested with:
      | dimension.country | dimension.before       | value |
      | "DE"              | "2007-01-01T00:00:00Z" | 0.16  |

  Scenario: Replacing a value with dimensions in body should fail if mismatch
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
      | "before"  | "string"    | "<"      | "ISO 8601"           |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" returns "400 Bad Request" when requested with:
      | dimensions.country | dimensions.before      | value |
      | "CH"               | "2007-01-01T00:00:00Z" | 0.16  |

  Scenario: Replacing a value should expose canonical value URL
    Given the following dimensions:
      | id        | schema.type | relation | description          |
      | "country" | "string"    | "="      | "ISO 3166-1 alpha-2" |
      | "before"  | "string"    | "<"      | "ISO 8601"           |
    And the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "PUT /keys/tax-rate/value?country=DE&before=2007-01-01T00:00:00Z" when requested with:
      | value |
      | 0.16  |
    Then "201 Created" was returned with headers:
      | Location                                                                         |
      | http://localhost:8080/keys/tax-rate/value?before=2007-01-01T00:00:00Z&country=DE |

  Scenario: Replacing a value failed due to schema violation
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    And "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values
    When "PUT /keys/tax-rate/value" when requested with:
      | value |
      | "19%" |
    Then "400 Bad Request" was returned with a list of violations:
      | field     | message                                  |
      | "$.value" | "$.value: string found, number expected" |

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
      | 0.19  |
    Then "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | value |
      | 0.19  |

  Scenario: Replacing values should allow to reorder
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
    When "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |
    And "GET /keys/tax-rate/values" returns "200 OK" with a list of values:
      | dimensions.country | value |
      | "DE"               | 0.19  |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |

  Scenario: Replacing values should return updated values
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
      | dimensions.country | value |
      | "DE"               | 0.19  |
      | "AT"               | 0.2   |
      | "CH"               | 0.08  |

  Scenario: Replacing values should delete
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
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |
    Then "200 OK" was returned with a list of values:
      | dimensions.country | value |
      | "CH"               | 0.08  |
      | "DE"               | 0.19  |

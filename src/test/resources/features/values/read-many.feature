Feature: Read values

  Scenario: List empty values
    Given the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And there are no values
    Then "GET /keys/tax-rate/values" returns "200 OK" with an empty list of values

  Scenario: List values
    Given the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /value |
      | 0.19   |
    When "GET /keys/tax-rate/values" returns "200 OK" with a list of /values:
      | /value |
      | 0.19   |

  Scenario: List values with dimensions
    Given the following dimensions:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "active"  | "boolean"    | "="       | ".."                 |
      | "age"     | "number"     | "<="      | ".."                 |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /dimensions/country | /dimensions/active | /dimensions/age | /value |
      | "DE"                | false              | 16              | 0.16   |
      | "DE"                | true               | 32              | 0.19   |
    Then "GET /keys/tax-rate/values?country=DE&active=true&age=27" returns "200 OK" with a list of /values:
      | /dimensions/country | /dimensions/active | /dimensions/age | /value |
      | "DE"                | true               | 32              | 0.19   |

  # TODO make it clear how matching values work, e.g. that we ignore additional filters when matching

  Scenario: List values with dimension present
    Given the following dimensions:
      | /id       | /schema/type | /relation | /description         |
      | "country" | "string"     | "="       | "ISO 3166-1 alpha-2" |
      | "before"  | "string"     | "<"       | "ISO 8601"           |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.16   |
    And the following values for key tax-rate:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
    Then "GET /keys/tax-rate/values?country&before" returns "200 OK" with an empty list of values
    Then "GET /keys/tax-rate/values?country" returns "200 OK" with a list of /values:
      | /dimensions/country | /value |
      | "DE"                | 0.19   |
    Then "GET /keys/tax-rate/values?before" returns "200 OK" with a list of /values:
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.16   |

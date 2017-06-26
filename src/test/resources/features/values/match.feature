Feature: Matching values

  Scenario Outline: Match values
    Given the following dimensions:
      | /id      | /schema/type | /relation    | /description |
      | "before" | "string"     | "<relation>" | "ISO 8601"   |
    And the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /dimensions/before     | /value |
      | "2007-01-01T00:00:00Z" | 0.19   |
      |                        | 0.16   |
    Then "GET /keys/tax-rate/value?before=<value>" returns "200 OK" with:
      | /value     |
      | <expected> |
    Examples:
      | relation | value                | expected |
      | <        | 2006-12-31T23:59:59Z | 0.19     |
      | <        | 2007-01-01T00:00:00Z | 0.16     |
      | <=       | 2006-12-31T23:59:59Z | 0.19     |
      | <=       | 2007-01-01T00:00:00Z | 0.19     |
      | <=       | 2007-01-01T00:00:01Z | 0.16     |
      | =        | 2006-12-31T23:59:59Z | 0.16     |
      | =        | 2007-01-01T00:00:00Z | 0.19     |
      | =        | 2007-01-01T00:00:01Z | 0.16     |
      | >=       | 2006-12-31T23:59:59Z | 0.16     |
      | >=       | 2007-01-01T00:00:00Z | 0.19     |
      | >=       | 2007-01-01T00:00:01Z | 0.19     |
      | >        | 2006-12-31T23:59:59Z | 0.16     |
      | >        | 2007-01-01T00:00:00Z | 0.16     |
      | >        | 2007-01-01T00:00:01Z | 0.19     |

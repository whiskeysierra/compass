Feature: Matching values

  Scenario Outline: Match values
    Given "PUT /dimensions/before" returns successfully when requested with:
      | /schema/type | /relation    | /description |
      | "string"     | "<relation>" | ".."         |
    And "PUT /keys/tax-rate" returns successfully when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/values" returns "200 OK" when requested with a list of /values:
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

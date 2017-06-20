Feature: Relations

  Scenario: List relations
    When "GET /relations" returns "200 OK" with a list of /relations:
      | /id  | /title                  |
      | "<"  | "Less than"             |
      | "<=" | "Less than or equal"    |
      | "="  | "Equality"              |
      | ">"  | "Greater than"          |
      | ">=" | "Greater than or equal" |
      | "^"  | "Prefix match"          |
      | "~"  | "Regular expression"    |

  Scenario: Get relation
    When "GET /relations/=" returns "200 OK" with:
      | /id | /title     | /description                                                                            |
      | "=" | "Equality" | "Matches values where the requested dimension values are equal to the configured ones." |

  Scenario: Get unknown relation
    Then "GET /relations/unknown" returns "404 Not Found"

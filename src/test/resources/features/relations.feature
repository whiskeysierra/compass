Feature: Relations

  Scenario: List relations
    When "GET /relations" is requested
    Then the following relations are returned:
      | id | title                 |
      | =  | Equality              |
      | >  | Greater than          |
      | >= | Greater than or equal |
      | <  | Less than             |
      | <= | Less than or equal    |
      | ~  | Matches               |
      | ^  | Longest Prefix Match  |

  Scenario: Get relation
    When "GET /relations/=" is requested
    Then the following is returned:
      | id | title    | description                                                                         |
      | =  | Equality | Matches values where the requested dimension values is equal to the configured one. |

  Scenario: Get unknown relation
    When "GET /relations/unknown" is requested
    Then "404 Not Found" is returned

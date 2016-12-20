Feature: Relations

  Scenario: List relations
    When "GET /relations" is requested
    Then the following relations are returned:
      | ID | Description           |
      | =  | Equality              |
      | >  | Greater than          |
      | >= | Greater than or equal |
      | <  | Less than             |
      | <= | Less than or equal    |
      | ~  | Matches               |
      | ^  | Prefix                |

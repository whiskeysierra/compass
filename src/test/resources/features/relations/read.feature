Feature: Relations

  Scenario: List relations
    When "GET /relations" responds "200 OK" with an array at "/relations":
      | /id  | /title                  |
      | "<"  | "Less than"             |
      | "<=" | "Less than or equal"    |
      | "="  | "Equality"              |
      | ">"  | "Greater than"          |
      | ">=" | "Greater than or equal" |
      | "^"  | "Prefix match"          |
      | "~"  | "Regular expression"    |

  Scenario Outline: Get relation
    When "GET /relations/<id>" responds "200 OK" with:
      | /id    | /title    |
      | "<id>" | "<title>" |
    Examples:
      | id | title                 |
      | <  | Less than             |
      | <= | Less than or equal    |
      | =  | Equality              |
      | >  | Greater than          |
      | >= | Greater than or equal |
      | ^  | Prefix match          |
      | ~  | Regular expression    |

  Scenario: Get unknown relation
    Then "GET /relations/unknown" responds "404 Not Found"

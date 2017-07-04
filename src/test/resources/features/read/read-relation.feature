Feature: Read relations

  Scenario Outline: Read relation
    Then "GET /relations/<id>" responds "200 OK" with:
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

  Scenario: Read^ unknown relation
    Then "GET /relations/unknown" responds "404 Not Found"

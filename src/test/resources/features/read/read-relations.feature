Feature: Read relations

  Scenario: Read relations
    When "GET /relations" responds "200 OK" with an array at "/relations":
      | /id  | /title                  |
      | "<"  | "Less than"             |
      | "<=" | "Less than or equal"    |
      | "="  | "Equality"              |
      | ">"  | "Greater than"          |
      | ">=" | "Greater than or equal" |
      | "^"  | "Prefix match"          |
      | "~"  | "Regular expression"    |

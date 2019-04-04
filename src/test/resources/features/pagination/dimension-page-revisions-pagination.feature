Feature: Dimension page revisions pagination

  Background: Updated dimensions
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/location" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "^"       | ".."         |
    And "PATCH /dimensions/device" responds "200 OK" when requested with:
      | /schema/enum      |
      | ["desktop","app"] |
    And "PATCH /dimensions/location" responds "200 OK" when requested with:
      | /description |
      | "Geo Hash"   |
    And "DELETE /dimensions/device" responds "204 No Content"

  Scenario: Read dimension revisions should paginate forward
    Then "GET /dimensions/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 5   | "update" | "anonymous" |
      | 4   | "update" | "anonymous" |
    And "GET /dimensions/revisions?limit=2" responds "200 OK" with:
      | /prev | /next                                                                            |
      |       | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 3   | "update" | "anonymous" |
      | 2   | "update" | "anonymous" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" responds "200 OK" with:
      | /prev                                                                            | /next                                                                            |
      | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 1   | "update" | "anonymous" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with:
      | /prev                                                                            | /next |
      | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" |       |

  Scenario: Read dimension revisions should paginate backward
    Then "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 1   | "update" | "anonymous" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with:
      | /prev                                                                            | /next |
      | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" |       |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 3   | "update" | "anonymous" |
      | 2   | "update" | "anonymous" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" responds "200 OK" with:
      | /prev                                                                            | /next                                                                            |
      | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 5   | "update" | "anonymous" |
      | 4   | "update" | "anonymous" |
    And "GET /dimensions/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" responds "200 OK" with:
      | /prev | /next                                                                            |
      |       | "http://localhost:8080/dimensions/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" |

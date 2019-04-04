Feature: Key revisions pagination

  Background: Updated key
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PATCH /keys/device" responds "200 OK" when requested with:
      | /schema/enum      |
      | ["desktop","app"] |
    And "PATCH /keys/device" responds "200 OK" when requested with:
      | /description               |
      | "Client Device Identifier" |
    And "PATCH /keys/device" responds "200 OK" when requested with:
      | /description |
      | ".."         |
    And "DELETE /keys/device" responds "204 No Content"

  Scenario: Read key revisions should paginate forward
    Then "GET /keys/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 5   | "delete" | "anonymous" |
      | 4   | "update" | "anonymous" |
    And "GET /keys/device/revisions?limit=2" responds "200 OK" with:
      | /prev | /next                                                                             |
      |       | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 3   | "update" | "anonymous" |
      | 2   | "update" | "anonymous" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" responds "200 OK" with:
      | /prev                                                                             | /next                                                                             |
      | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 1   | "create" | "anonymous" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with:
      | /prev                                                                             | /next |
      | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" |       |

  Scenario: Read key revisions should paginate backward
    Then "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 1   | "create" | "anonymous" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" responds "200 OK" with:
      | /prev                                                                             | /next |
      | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" |       |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 3   | "update" | "anonymous" |
      | 2   | "update" | "anonymous" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjF9" responds "200 OK" with:
      | /prev                                                                             | /next                                                                             |
      | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjJ9" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 5   | "delete" | "anonymous" |
      | 4   | "update" | "anonymous" |
    And "GET /keys/device/revisions?limit=2&cursor=eyJkIjoiPCIsInAiOjN9" responds "200 OK" with:
      | /prev | /next                                                                             |
      |       | "http://localhost:8080/keys/device/revisions?limit=2&cursor=eyJkIjoiPiIsInAiOjR9" |

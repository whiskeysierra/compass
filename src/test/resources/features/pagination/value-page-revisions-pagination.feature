Feature: Value page revisions pagination

  Background: Updated value
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device/value" responds "201 Created" when requested with:
      | /value |
      | "a"    |
    And "PATCH /keys/device/value" responds "200 OK" when requested with:
      | /value |
      | "b"    |
    And "PATCH /keys/device/value" responds "200 OK" when requested with:
      | /value |
      | "c"    |
    And "PATCH /keys/device/value" responds "200 OK" when requested with:
      | /value |
      | "d"    |
    And "DELETE /keys/device/value" responds "204 No Content"

  Scenario: Read value page revisions should paginate forward
    Then "GET /keys/device/values/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 6   | "update" | "anonymous" |
      | 5   | "update" | "anonymous" |
    And "GET /keys/device/values/revisions?limit=2" responds "200 OK" with:
      | /prev | /next                                                                                    |
      |       | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjUsImwiOjJ9" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjUsImwiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 4   | "update" | "anonymous" |
      | 3   | "update" | "anonymous" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjUsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                    | /next                                                                                    |
      | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjQsImwiOjJ9" | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjMsImwiOjJ9" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjMsImwiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 2   | "update" | "anonymous" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjMsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                    | /next |
      | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjIsImwiOjJ9" |       |

  Scenario: Read value page revisions should paginate backward
    Then "GET /keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjMsImwiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 2   | "update" | "anonymous" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjMsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                    | /next |
      | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjIsImwiOjJ9" |       |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjIsImwiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 4   | "update" | "anonymous" |
      | 3   | "update" | "anonymous" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjIsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                    | /next                                                                                    |
      | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjQsImwiOjJ9" | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjMsImwiOjJ9" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjQsImwiOjJ9" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 6   | "update" | "anonymous" |
      | 5   | "update" | "anonymous" |
    And "GET /keys/device/values/revisions?cursor=eyJkIjoiPCIsInAiOjQsImwiOjJ9" responds "200 OK" with:
      | /prev | /next                                                                                    |
      |       | "http://localhost:8080/keys/device/values/revisions?cursor=eyJkIjoiPiIsInAiOjUsImwiOjJ9" |

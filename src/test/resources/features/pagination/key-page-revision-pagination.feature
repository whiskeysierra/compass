Feature: Key pagination

  Background: Updated keys
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/location" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/country" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/language" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/age" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "integer"    | ".."         |

  Scenario: Read keys should paginate forward
    Then "GET /keys/revisions/5?limit=2" responds "200 OK" with an array at "/keys":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /keys/revisions/5?limit=2" responds "200 OK" with:
      | /prev | /next                                                                                   |
      |       | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5IiwibCI6Mn0" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5IiwibCI6Mn0" responds "200 OK" with an array at "/keys":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5IiwibCI6Mn0" responds "200 OK" with:
      | /prev                                                                                  | /next                                                                                    |
      | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UiLCJsIjoyfQ" | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSIsImwiOjJ9" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSIsImwiOjJ9" responds "200 OK" with an array at "/keys":
      | /id        |
      | "location" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSIsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                    | /next |
      | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiIsImwiOjJ9" |       |

  Scenario: Read key revisions should paginate backward
    Then "GET /keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSIsImwiOjJ9" responds "200 OK" with an array at "/keys":
      | /id        |
      | "location" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSIsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                    | /next |
      | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiIsImwiOjJ9" |       |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiIsImwiOjJ9" responds "200 OK" with an array at "/keys":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiIsImwiOjJ9" responds "200 OK" with:
      | /prev                                                                                  | /next                                                                                    |
      | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UiLCJsIjoyfQ" | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSIsImwiOjJ9" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UiLCJsIjoyfQ" responds "200 OK" with an array at "/keys":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /keys/revisions/5?cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UiLCJsIjoyfQ" responds "200 OK" with:
      | /prev | /next                                                                                   |
      |       | "http://localhost:8080/keys/revisions/5?cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5IiwibCI6Mn0" |

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
    Then "GET /keys?limit=2" responds "200 OK" with an array at "/keys":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /keys?limit=2" responds "200 OK" with:
      | /prev | /next                                                                       |
      |       | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" responds "200 OK" with an array at "/keys":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" responds "200 OK" with:
      | /prev                                                                      | /next                                                                        |
      | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with an array at "/keys":
      | /id        |
      | "location" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with:
      | /prev                                                                        | /next |
      | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" |       |

  Scenario: Read key revisions should paginate backward
    Then "GET /keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with an array at "/keys":
      | /id        |
      | "location" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with:
      | /prev                                                                        | /next |
      | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" |       |
    And "GET /keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" responds "200 OK" with an array at "/keys":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" responds "200 OK" with:
      | /prev                                                                      | /next                                                                        |
      | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" responds "200 OK" with an array at "/keys":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /keys?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" responds "200 OK" with:
      | /prev | /next                                                                       |
      |       | "http://localhost:8080/keys?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" |

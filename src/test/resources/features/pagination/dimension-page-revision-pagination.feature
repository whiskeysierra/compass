Feature: Dimension pagination

  Background: Updated dimensions
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/location" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "^"       | ".."         |
    And "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/language" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/age" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "integer"    | "="       | ".."         |

  Scenario: Read dimensions should paginate forward
    Then "GET /dimensions/revisions/5?limit=2" responds "200 OK" with an array at "/dimensions":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /dimensions/revisions/5?limit=2" responds "200 OK" with:
      | /prev | /next                                                                                         |
      |       | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" responds "200 OK" with:
      | /prev                                                                                        | /next                                                                                          |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "location" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with:
      | /prev                                                                                          | /next |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" |       |

  Scenario: Read dimension revisions should paginate backward
    Then "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "location" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" responds "200 OK" with:
      | /prev                                                                                          | /next |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" |       |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJsb2NhdGlvbiJ9" responds "200 OK" with:
      | /prev                                                                                        | /next                                                                                          |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJsYW5ndWFnZSJ9" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" responds "200 OK" with an array at "/dimensions":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /dimensions/revisions/5?limit=2&cursor=eyJkIjoiPCIsInAiOiJkZXZpY2UifQ" responds "200 OK" with:
      | /prev | /next                                                                                         |
      |       | "http://localhost:8080/dimensions/revisions/5?limit=2&cursor=eyJkIjoiPiIsInAiOiJjb3VudHJ5In0" |

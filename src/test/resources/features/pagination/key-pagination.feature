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
      | /prev | /next                                               |
      |       | "http://localhost:8080/keys?limit=2&_after=country" |
    And "GET /keys?limit=2&_after=country" responds "200 OK" with an array at "/keys":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /keys?limit=2&_after=country" responds "200 OK" with:
      | /prev                                               | /next                                                |
      | "http://localhost:8080/keys?limit=2&_before=device" | "http://localhost:8080/keys?limit=2&_after=language" |
    And "GET /keys?limit=2&_after=language" responds "200 OK" with an array at "/keys":
      | /id        |
      | "location" |
    And "GET /keys?limit=2&_after=language" responds "200 OK" with:
      | /prev                                                 | /next |
      | "http://localhost:8080/keys?limit=2&_before=location" |       |

  Scenario: Read key revisions should paginate backward
    Then "GET /keys?limit=2&_after=language" responds "200 OK" with an array at "/keys":
      | /id        |
      | "location" |
    And "GET /keys?limit=2&_after=language" responds "200 OK" with:
      | /prev                                                 | /next |
      | "http://localhost:8080/keys?limit=2&_before=location" |       |
    And "GET /keys?limit=2&_before=location" responds "200 OK" with an array at "/keys":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /keys?limit=2&_before=location" responds "200 OK" with:
      | /prev                                               | /next                                                |
      | "http://localhost:8080/keys?limit=2&_before=device" | "http://localhost:8080/keys?limit=2&_after=language" |
    And "GET /keys?limit=2&_before=device" responds "200 OK" with an array at "/keys":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /keys?limit=2&_before=device" responds "200 OK" with:
      | /prev | /next                                               |
      |       | "http://localhost:8080/keys?limit=2&_after=country" |

  Scenario: Pagination with conflicting directions should fail
    Then "GET /keys?_after=3&_before=1" responds "400 Bad Request"

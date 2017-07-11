Feature: Value revisions pagination

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

  Scenario: Read value revisions should paginate forward
    Then "GET /keys/device/value/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 6   | "delete" | "anonymous" |
      | 5   | "update" | "anonymous" |
    And "GET /keys/device/value/revisions?limit=2" responds "200 OK" with:
      | /prev | /next                                                                 |
      |       | "http://localhost:8080/keys/device/value/revisions?limit=2&_after=5" |
    And "GET /keys/device/value/revisions?limit=2&_after=5" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 4   | "update" | "anonymous" |
      | 3   | "update" | "anonymous" |
    And "GET /keys/device/value/revisions?limit=2&_after=5" responds "200 OK" with:
      | /prev                                                                  | /next                                                                 |
      | "http://localhost:8080/keys/device/value/revisions?limit=2&_before=4" | "http://localhost:8080/keys/device/value/revisions?limit=2&_after=3" |
    And "GET /keys/device/value/revisions?limit=2&_after=3" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 2   | "create" | "anonymous" |
    And "GET /keys/device/value/revisions?limit=2&_after=3" responds "200 OK" with:
      | /prev                                                                  | /next |
      | "http://localhost:8080/keys/device/value/revisions?limit=2&_before=2" |       |

  Scenario: Read value revisions should paginate backward
    Then "GET /keys/device/value/revisions?limit=2&_after=3" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 2   | "create" | "anonymous" |
    And "GET /keys/device/value/revisions?limit=2&_after=3" responds "200 OK" with:
      | /prev                                                                  | /next |
      | "http://localhost:8080/keys/device/value/revisions?limit=2&_before=2" |       |
    And "GET /keys/device/value/revisions?limit=2&_before=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 4   | "update" | "anonymous" |
      | 3   | "update" | "anonymous" |
    And "GET /keys/device/value/revisions?limit=2&_before=2" responds "200 OK" with:
      | /prev                                                                  | /next                                                                 |
      | "http://localhost:8080/keys/device/value/revisions?limit=2&_before=4" | "http://localhost:8080/keys/device/value/revisions?limit=2&_after=3" |
    And "GET /keys/device/value/revisions?limit=2&_before=4" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 6   | "delete" | "anonymous" |
      | 5   | "update" | "anonymous" |
    And "GET /keys/device/value/revisions?limit=2&_before=4" responds "200 OK" with:
      | /prev | /next                                                                 |
      |       | "http://localhost:8080/keys/device/value/revisions?limit=2&_after=5" |

  Scenario: Pagination with conflicting directions should fail
    Then "GET /keys/device/value/revisions?_after=3&_before=1" responds "400 Bad Request"

Feature: Dimension revisions

  Background: Updated dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PATCH /dimensions/device" responds "200 OK" when requested with:
      | /schema/enum      |
      | ["desktop","app"] |
    And "PATCH /dimensions/device" responds "200 OK" when requested with:
      | /description               |
      | "Client Device Identifier" |
    And "PATCH /dimensions/device" responds "200 OK" when requested with:
      | /relation |
      | "~"       |
    And "DELETE /dimensions/device" responds "204 No Content"

  Scenario: Read dimension revisions should paginate forward
    Then "GET /dimensions/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 5   | "delete" | "anonymous" |
      | 4   | "update" | "anonymous" |
    And "GET /dimensions/device/revisions?limit=2" responds "200 OK" with:
      | /prev | /next                                                                |
      |       | "http://localhost:8080/dimensions/device/revisions?limit=2&_after=4" |
    And "GET /dimensions/device/revisions?limit=2&_after=4" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 3   | "update" | "anonymous" |
      | 2   | "update" | "anonymous" |
    And "GET /dimensions/device/revisions?limit=2&_after=4" responds "200 OK" with:
      | /prev                                                                 | /next                                                                |
      | "http://localhost:8080/dimensions/device/revisions?limit=2&_before=3" | "http://localhost:8080/dimensions/device/revisions?limit=2&_after=2" |
    And "GET /dimensions/device/revisions?limit=2&_after=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 1   | "create" | "anonymous" |
    And "GET /dimensions/device/revisions?limit=2&_after=2" responds "200 OK" with:
      | /prev                                                                 | /next |
      | "http://localhost:8080/dimensions/device/revisions?limit=2&_before=1" |       |

  Scenario: Read dimension revisions should paginate backward
    Then "GET /dimensions/device/revisions?limit=2&_after=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 1   | "create" | "anonymous" |
    And "GET /dimensions/device/revisions?limit=2&_after=2" responds "200 OK" with:
      | /prev                                                                 | /next |
      | "http://localhost:8080/dimensions/device/revisions?limit=2&_before=1" |       |
    And "GET /dimensions/device/revisions?limit=2&_before=1" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 3   | "update" | "anonymous" |
      | 2   | "update" | "anonymous" |
    And "GET /dimensions/device/revisions?limit=2&_before=1" responds "200 OK" with:
      | /prev                                                                 | /next                                                                |
      | "http://localhost:8080/dimensions/device/revisions?limit=2&_before=3" | "http://localhost:8080/dimensions/device/revisions?limit=2&_after=2" |
    And "GET /dimensions/device/revisions?limit=2&_before=3" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       |
      | 5   | "delete" | "anonymous" |
      | 4   | "update" | "anonymous" |
    And "GET /dimensions/device/revisions?limit=2&_before=3" responds "200 OK" with:
      | /prev | /next                                                                |
      |       | "http://localhost:8080/dimensions/device/revisions?limit=2&_after=4" |

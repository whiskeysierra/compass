Feature: Dimension revisions

  Background: Updated dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |
    And "DELETE /dimensions/device" responds "204 No Content"

  Scenario: Read dimension revisions should paginate
    Then "GET /dimensions/device/revisions?limit=2" responds "200 OK" with:
      | /next                                                               |
      | "http://localhost:8080/dimensions/device/revisions?limit=2&after=2" |
    And "GET /dimensions/device/revisions?limit=2&after=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 1   | "create" | "anonymous" |          |
    Then "GET /dimensions/device/revisions?limit=2&after=2" responds "200 OK" with:
      | /next |
      |       |

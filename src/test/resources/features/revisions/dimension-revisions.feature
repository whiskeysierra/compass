Feature: /dimensions/{id}/revisions

  Background: Updated dimension
    Given "PUT /dimensions/device" and "Comment: Created dimension" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" and "Comment: Updated dimension" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "="       | "Client Device Identifier" |
    And "DELETE /dimensions/device" and "Comment: Deleted dimension" responds "204 No Content"
    Given "PUT /dimensions/device" and "Comment: Recreated dimension" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |

  Scenario: Read revisions
    Then "GET /dimensions/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /href                                                 | /type    | /user       | /comment              |
      | 5   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/5" | "update" | "anonymous" |                       |
      | 4   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/4" | "create" | "anonymous" | "Recreated dimension" |
      | 3   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/3" | "delete" | "anonymous" | "Deleted dimension"   |
      | 2   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/2" | "update" | "anonymous" | "Updated dimension"   |
      | 1   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/1" | "create" | "anonymous" | "Created dimension"   |

  Scenario: Read revisions should support limit
    Then "GET /dimensions/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /href                                                 | /type    | /user       | /comment              |
      | 5   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/5" | "update" | "anonymous" |                       |
      | 4   | "2017-07-07T22:09:21Z" | "http://localhost:8080/dimensions/device/revisions/4" | "create" | "anonymous" | "Recreated dimension" |

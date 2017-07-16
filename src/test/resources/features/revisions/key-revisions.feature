Feature: /keys/{id}/revisions

  Background: Updated key
    Given "PUT /keys/device" and "Comment: Created key" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" and "Comment: Updated key" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" and "Comment: Deleted key" responds "204 No Content"
    And "PUT /keys/device" and "Comment: Recreated key" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |

  Scenario: Read revisions
    Then "GET /keys/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /href                                           | /type    | /user       | /comment        |
      | 5   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/5" | "update" | "anonymous" |                 |
      | 4   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/4" | "create" | "anonymous" | "Recreated key" |
      | 3   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/3" | "delete" | "anonymous" | "Deleted key"   |
      | 2   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/2" | "update" | "anonymous" | "Updated key"   |
      | 1   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/1" | "create" | "anonymous" | "Created key"   |

  Scenario: Read revisions should support limit
    Then "GET /keys/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /href                                           | /type    | /user       | /comment        |
      | 5   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/5" | "update" | "anonymous" |                 |
      | 4   | "2017-07-07T22:09:21Z" | "http://localhost:8080/keys/device/revisions/4" | "create" | "anonymous" | "Recreated key" |

Feature: /keys/{id}/revisions

  Background: Updated key
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds "204 No Content"
    And "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |

  Scenario: Read revisions
    Then "GET /keys/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |
      | 4   | "2017-07-07T22:09:21Z" | "create" | "anonymous" | ".."     |
      | 3   | "2017-07-07T22:09:21Z" | "delete" | "anonymous" | ".."     |
      | 2   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |
      | 1   | "2017-07-07T22:09:21Z" | "create" | "anonymous" | ".."     |

  Scenario: Read revisions should support limit
    Then "GET /keys/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |
      | 4   | "2017-07-07T22:09:21Z" | "create" | "anonymous" | ".."     |

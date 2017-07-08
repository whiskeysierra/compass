Feature: /dimensions/{id}/revisions

  Background: Updated dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |
    And "DELETE /dimensions/device" responds "204 No Content"
    And "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |

  Scenario: Read revisions
    Then "GET /dimensions/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |
      | 4   | "2017-07-07T22:09:21Z" | "create" | "anonymous" | ".."     |
      | 3   | "2017-07-07T22:09:21Z" | "delete" | "anonymous" | ".."     |
      | 2   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |
      | 1   | "2017-07-07T22:09:21Z" | "create" | "anonymous" | ".."     |

  Scenario: Read revisions should support limit
    Then "GET /dimensions/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |
      | 4   | "2017-07-07T22:09:21Z" | "create" | "anonymous" | ".."     |

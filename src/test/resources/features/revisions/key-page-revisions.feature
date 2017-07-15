Feature: /keys/revisions

  Background: Updated keys
    Given "PUT /keys/device" and "Comment: Created key" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" and "Comment: Updated key" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" and "Comment: Deleted key" responds "204 No Content"
    Given "PUT /keys/country" and "Comment: Recreated key" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/country" responds "200 OK" when requested with:
      | /schema/type | /description |
      | "string"     | "ISO 3166"   |

  Scenario: Read revisions
    Then "GET /keys/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment        |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" |                 |
      | 4   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Recreated key" |
      | 3   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Deleted key"   |
      | 2   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Updated key"   |
      | 1   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Created key"   |

  Scenario: Read revisions should support limit
    Then "GET /keys/revisions?limit=3" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment        |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" |                 |
      | 4   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Recreated key" |
      | 3   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Deleted key"   |
Feature: /keys/{id}/revisions/{revision}

  Background: Updated key
    Given "PUT /keys/device" and "Comment: Created key" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" and "Comment: Updated key" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" and "Comment: Deleted key" responds "204 No Content"

  Scenario: Read revision
    Then "GET /keys/device/revisions/1" responds "200 OK" with:
      | /id      | /revision/id | /revision/timestamp    | /revision/href | /revision/type | /revision/user | /revision/comment | /schema/type | /description |
      | "device" | 1            | "2017-07-07T22:09:21Z" |                | "create"       | "anonymous"    | "Created key"     | "string"     | ".."         |
    And "GET /keys/device/revisions/2" responds "200 OK" with:
      | /id      | /revision/id | /revision/timestamp    | /revision/href | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 2            | "2017-07-07T22:09:21Z" |                | "update"       | "anonymous"    | "Updated key"     | "string"     | "Client Device Identifier" |
    And "GET /keys/device/revisions/3" responds "200 OK" with:
      | /id      | /revision/id | /revision/timestamp    | /revision/href | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "2017-07-07T22:09:21Z" |                | "delete"       | "anonymous"    | "Deleted key"     | "string"     | "Client Device Identifier" |

Feature: /keys/{id}/revisions/{revision}

  Background: Updated key
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds "204 No Content"

  Scenario: Read revision
    Then "GET /keys/device/revisions/1" responds "200 OK" with:
      | /id      | /revision/id | /revision/timestamp    | /revision/type | /revision/user | /revision/comment | /schema/type | /description |
      | "device" | 1            | "2017-07-07T22:09:21Z" | "create"       | "anonymous"    | ".."              | "string"     | ".."         |
    And "GET /keys/device/revisions/2" responds "200 OK" with:
      | /id      | /revision/id | /revision/timestamp    | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 2            | "2017-07-07T22:09:21Z" | "update"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
    And "GET /keys/device/revisions/3" responds "200 OK" with:
      | /id      | /revision/id | /revision/timestamp    | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "2017-07-07T22:09:21Z" | "delete"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |

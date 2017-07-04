Feature: Key history

  Background: Updated key
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds "204 No Content"

  Scenario: Read key revisions
    Then "GET /keys/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | ".."                       |

  Scenario: Read key revisions should support limit
    Then "GET /keys/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |

  Scenario: Access all key revisions
    Then "GET /keys/revisions" responds "200 OK"

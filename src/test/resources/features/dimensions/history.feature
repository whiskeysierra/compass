Feature: Dimension history

  Background: Updated dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |
    And "DELETE /dimensions/device" responds "204 No Content"

  Scenario: Read dimension revisions
    Then "GET /dimensions/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | "="       | ".."                       |

  # TODO pagination
  Scenario: Read dimension revisions should support limit
    Then "GET /dimensions/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |

  Scenario: Access all dimension revisions
    Then "GET /dimensions/revisions" responds "200 OK"

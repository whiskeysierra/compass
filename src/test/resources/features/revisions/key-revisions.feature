Feature: Key history

  Background: Updated key
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds "204 No Content"

  Scenario: Read revisions
    Then "GET /keys/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 3   | "delete" | "anonymous" | ".."     |
      | 2   | "update" | "anonymous" | ".."     |
      | 1   | "create" | "anonymous" | ".."     |

  Scenario: Read revision
    Then "GET /keys/device/revisions/1" responds "200 OK" with:
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | ".."         |
    And "GET /keys/device/revisions/2" responds "200 OK" with:
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
    And "GET /keys/device/revisions/3" responds "200 OK" with:
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |

  Scenario: Read revisions should support limit
    Then "GET /keys/device/revisions?limit=2" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 3   | "delete" | "anonymous" | ".."     |
      | 2   | "update" | "anonymous" | ".."     |

  Scenario: Re-create key
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    Then "GET /keys/device/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "create" | "anonymous" | ".."     |
      | 3   | "delete" | "anonymous" | ".."     |
      | 2   | "update" | "anonymous" | ".."     |
      | 1   | "create" | "anonymous" | ".."     |

Feature: Key history

  Background: Updated key
    Given "PUT /keys/device" responds successfully when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds successfully when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds successfully

  Scenario: Read key revisions
    Then "GET /keys/device/revisions" responds successfully with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | ".."                       |

  Scenario: Read key revisions should support limit
    Then "GET /keys/device/revisions?limit=2" responds successfully with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "Client Device Identifier" |

  Scenario: Read deleted key
    Then "GET /keys/device" responds "410 Gone" with headers:
      | Location                                    |
      | http://localhost:8080/keys/device/revisions |

  Scenario: Access all key revisions
    Then "GET /keys/revisions" responds successfully

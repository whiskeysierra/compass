Feature: Dimension history

  Background: Updated dimension
    Given "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |
    And "DELETE /dimensions/device" responds successfully

  Scenario: Read dimension revisions
    Then "GET /dimensions/device/revisions" responds successfully with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | "="       | ".."                       |

  Scenario: Read dimension revisions should support limit
    Then "GET /dimensions/device/revisions?limit=2" responds successfully with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |

  Scenario: Read deleted dimension
    Then "GET /dimensions/device" responds "410 Gone" with headers:
      | Location                                          |
      | http://localhost:8080/dimensions/device/revisions |

  Scenario: Access all dimension revisions
    Then "GET /dimensions/revisions" responds successfully

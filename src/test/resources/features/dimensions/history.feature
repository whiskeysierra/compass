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
    Then "GET /dimensions/device/revisions" responds successfully with an array at "/dimensions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | "="       | ".."                       |

  Scenario: Read dimension revisions should support limit
    Then "GET /dimensions/device/revisions?limit=2" responds successfully with an array at "/dimensions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |

  Scenario: Read dimension revision
    Then "GET /dimensions/device/revisions/1" responds successfully with:
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | "="       | ".."         |
    And "GET /dimensions/device/revisions/2" responds successfully with:
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 2            | "update"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |
    And "GET /dimensions/device/revisions/3" responds successfully with:
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description               |
      | "device" | 3            | "delete"       | "anonymous"    | ".."              | "string"     | "~"       | "Client Device Identifier" |

  Scenario: Read deleted dimension
    Then "GET /dimensions/device" responds "410 Gone" with headers:
      | Location                                            |
      | http://localhost:8080/dimensions/device/revisions/3 |

  Scenario: Access all dimension revisions
    Then "GET /dimensions/revisions" responds successfully

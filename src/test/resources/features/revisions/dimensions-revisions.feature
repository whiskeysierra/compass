Feature: Dimensions history

  Background: Updated dimensions
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds "200 OK" when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "="       | "Client Device Identifier" |
    And "DELETE /dimensions/device" responds "204 No Content"
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/country" responds "200 OK" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | "ISO 3166"   |

  Scenario: Read revisions
    Then "GET /dimensions/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "update" | "anonymous" | ".."     |
      | 3   | "update" | "anonymous" | ".."     |
      | 2   | "update" | "anonymous" | ".."     |
      | 1   | "update" | "anonymous" | ".."     |

  Scenario: Read revision
    Then "GET /dimensions/revisions/1" responds "200 OK" with an array at "/dimensions":
      | /id      | /schema/type | /relation | /description |
      | "device" | "string"     | "="       | ".."         |
    And "GET /dimensions/revisions/2" responds "200 OK" with an array at "/dimensions":
      | /id      | /schema/type | /relation | /description               |
      | "device" | "string"     | "="       | "Client Device Identifier" |
    And "GET /dimensions/revisions/3" responds "200 OK" with an empty array at "/dimensions"
    And "GET /dimensions/revisions/4" responds "200 OK" with an array at "/dimensions":
      | /id       | /schema/type | /relation | /description |
      | "country" | "string"     | "="       | ".."         |
    And "GET /dimensions/revisions/5" responds "200 OK" with an array at "/dimensions":
      | /id       | /schema/type | /relation | /description |
      | "country" | "string"     | "="       | "ISO 3166"   |

  Scenario: Read revision metadata
    Then "GET /dimensions/revisions/1" responds "200 OK" with:
      | /revision/id | /revision/type | /revision/user | /revision/comment |
      | 1            | "update"       | "anonymous"    | ".."              |

  Scenario: Read revisions should support limit
    Then "GET /dimensions/revisions?limit=3" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "update" | "anonymous" | ".."     |
      | 3   | "update" | "anonymous" | ".."     |

Feature: /dimensions/revisions

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

  Scenario: Read revisions should support limit
    Then "GET /dimensions/revisions?limit=3" responds "200 OK" with an array at "/revisions":
      | /id | /type    | /user       | /comment |
      | 5   | "update" | "anonymous" | ".."     |
      | 4   | "update" | "anonymous" | ".."     |
      | 3   | "update" | "anonymous" | ".."     |

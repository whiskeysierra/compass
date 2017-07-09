Feature: /dimensions/revisions/{revision}

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
    Then "GET /dimensions/revisions/1" responds "200 OK" with at "/revision":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 1   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |

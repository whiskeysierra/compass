Feature: /keys/revisions/{revision}

  Background: Updated keys
    Given "PUT /keys/device" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds "200 OK" when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds "204 No Content"
    Given "PUT /keys/country" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/country" responds "200 OK" when requested with:
      | /schema/type | /description |
      | "string"     | "ISO 3166"   |

  Scenario: Read revision
    Then "GET /keys/revisions/1" responds "200 OK" with an array at "/keys":
      | /id      | /schema/type | /description |
      | "device" | "string"     | ".."         |
    And "GET /keys/revisions/2" responds "200 OK" with an array at "/keys":
      | /id      | /schema/type | /description               |
      | "device" | "string"     | "Client Device Identifier" |
    And "GET /keys/revisions/3" responds "200 OK" with an empty array at "/keys"
    And "GET /keys/revisions/4" responds "200 OK" with an array at "/keys":
      | /id       | /schema/type | /description |
      | "country" | "string"     | ".."         |
    And "GET /keys/revisions/5" responds "200 OK" with an array at "/keys":
      | /id       | /schema/type | /description |
      | "country" | "string"     | "ISO 3166"   |

  Scenario: Read revision metadata
    Then "GET /keys/revisions/1" responds "200 OK" with at "/revision":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 1   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | ".."     |

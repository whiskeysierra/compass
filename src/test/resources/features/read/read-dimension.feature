Feature: Reading dimension

  Scenario: Read dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    Then "GET /dimensions/device" responds "200 OK" with:
      | /id      | /schema/type | /relation | /description |
      | "device" | "string"     | "="       | ".."         |

  Scenario: Last modified and ETag
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    Then "GET /dimensions/device" responds "200 OK" with headers:
      | ETag          | Last-Modified                 |
      | "AAAAAAAAAAE" | Fri, 07 Jul 2017 22:09:21 GMT |

  Scenario: Read unknown dimension
    Then "GET /dimensions/unknwon" responds "404 Not Found"

  Scenario: Read deleted dimension
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "DELETE /dimensions/device" responds "204 No Content"
    Then "GET /dimensions/device" responds "404 Not Found"

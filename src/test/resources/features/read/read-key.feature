Feature: Read key

  Scenario: Read key
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    When "GET /keys/tax-rate" responds "200 OK" with:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |

  Scenario: Last modified and ETag
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    Then "GET /keys/tax-rate" responds "200 OK" with headers:
      | ETag          | Last-Modified                 |
      | "AAAAAAAAAAE" | Fri, 07 Jul 2017 22:09:21 GMT |

  Scenario: Read unknown key
    Then "GET /keys/unknown" responds "404 Not Found"

  Scenario: Read deleted key
    Given "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "DELETE /keys/tax-rate" responds "204 No Content"
    Then "GET /keys/tax-rate" responds "404 Not Found"

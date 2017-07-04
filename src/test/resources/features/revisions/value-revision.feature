Feature: Value history

  Background: Dimensions and key
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |

  Scenario: Read value revision
    When "PUT /keys/tax-rate/value?country=DE" responds "201 Created" when requested with:
      | /value |
      | 0.16    |
    And "PUT /keys/tax-rate/value?country=DE" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/values?country=DE" responds "204 No Content"
    Then "GET /keys/tax-rate/value/revisions/3?country=DE" responds "200 OK" with:
      | /dimensions/country | /revision/id | /revision/type | /revision/user | /revision/comment | /value |
      | "DE"                | 3            | "create"       | "anonymous"    | ".."              | 0.16   |
    And "GET /keys/tax-rate/value/revisions/4?country=DE" responds "200 OK" with:
      | /dimensions/country | /revision/id | /revision/type | /revision/user | /revision/comment | /value |
      | "DE"                | 4            | "update"       | "anonymous"    | ".."              | 0.19   |
    And "GET /keys/tax-rate/value/revisions/5?country=DE" responds "200 OK" with:
      | /dimensions/country | /revision/id | /revision/type | /revision/user | /revision/comment | /value |
      | "DE"                | 5            | "delete"       | "anonymous"    | ".."              | 0.19   |

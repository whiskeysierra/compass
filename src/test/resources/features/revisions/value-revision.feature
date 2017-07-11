Feature: /keys/{id}/value/revision/{revision}

  Background: Dimensions and key
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description         |
      | "string"     | "="       | "ISO 3166-1 alpha-2" |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/feature.active" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "boolean"    | ".."         |

  Scenario: Read revision
    When "PUT /keys/tax-rate/value?country=DE" and "Comment: Created DE tax rate" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    And "PUT /keys/tax-rate/value?country=DE" and "Comment: Fixed DE tax rate" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/value?country=DE" and "Comment: Dropped DE tax rate" responds "204 No Content"
    And "PUT /keys/tax-rate/values" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |
    Then "GET /keys/tax-rate/value/revisions/4?country=DE" responds "200 OK" with:
      | /dimensions/country | /revision/id | /revision/timestamp    | /revision/type | /revision/user | /revision/comment     | /value |
      | "DE"                | 4            | "2017-07-07T22:09:21Z" | "create"       | "anonymous"    | "Created DE tax rate" | 0.16   |
    And "GET /keys/tax-rate/value/revisions/5?country=DE" responds "200 OK" with:
      | /dimensions/country | /revision/id | /revision/timestamp    | /revision/type | /revision/user | /revision/comment   | /value |
      | "DE"                | 5            | "2017-07-07T22:09:21Z" | "update"       | "anonymous"    | "Fixed DE tax rate" | 0.19   |
    And "GET /keys/tax-rate/value/revisions/6?country=DE" responds "200 OK" with:
      | /dimensions/country | /revision/id | /revision/timestamp    | /revision/type | /revision/user | /revision/comment     | /value |
      | "DE"                | 6            | "2017-07-07T22:09:21Z" | "delete"       | "anonymous"    | "Dropped DE tax rate" | 0.19   |

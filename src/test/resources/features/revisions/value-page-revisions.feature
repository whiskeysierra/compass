Feature: /keys/{id}/values/revision

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

  Scenario: Updates values and read revisions
    When "PUT /keys/tax-rate/values" and "Comment: DACH tax rates" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "CH"                | 0.08   |
      | "DE"                | 0.16   |
      | "AT"                | 0.2    |
    And "PUT /keys/tax-rate/values" and "Comment: Reordered tax rates" responds "200 OK" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "DE"                | 0.19   |
    And "PUT /keys/tax-rate/values" and "Comment: Dropped DE and created FR tax rate" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |
    And "PUT /keys/feature.active/value" responds "201 Created" when requested with:
      | /value |
      | true   |
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment                             |
      | 6   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Dropped DE and created FR tax rate" |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Reordered tax rates"                |
      | 4   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "DACH tax rates"                     |

  Scenario: Update value and read revisions
    Given "PUT /keys/tax-rate/value?country=DE" and "Comment: Added DE tax rate" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    And "PUT /keys/tax-rate/value?country=DE" and "Comment: Fixed DE tax rate" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/value?country=DE" and "Comment: Dropped DE tax rate" responds "204 No Content"
    And "PUT /keys/tax-rate/value?country=DE" and "Comment: Re-added DE tax rate" responds "201 Created" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/value?country=DE" and "Comment: Dropped DE tax rate again" responds "204 No Content"
    And "PUT /keys/tax-rate/values" and "Comment: Added non-DE tax rates" responds "201 Created" when requested with an array at "/values":
      | /dimensions/country | /value |
      | "AT"                | 0.2    |
      | "CH"                | 0.08   |
      | "FR"                | 0.2    |
    And "PUT /keys/feature.active/value" responds "201 Created" when requested with:
      | /value |
      | true   |
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment                    |
      | 9   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Added non-DE tax rates"    |
      | 8   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Dropped DE tax rate again" |
      | 7   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Re-added DE tax rate"      |
      | 6   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Dropped DE tax rate"       |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Fixed DE tax rate"         |
      | 4   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Added DE tax rate"         |

  Scenario: Read value revisions without dimensions
    When "PUT /keys/tax-rate/value" and "Comment: Added DE tax rate" responds "201 Created" when requested with:
      | /value |
      | 0.16   |
    And "PUT /keys/tax-rate/value" and "Comment: Fixed DE tax rate" responds "200 OK" when requested with:
      | /value |
      | 0.19   |
    And "DELETE /keys/tax-rate/value" and "Comment: Dropped DE tax rate" responds "204 No Content"
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment              |
      | 6   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Dropped DE tax rate" |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Fixed DE tax rate"   |
      | 4   | "2017-07-07T22:09:21Z" | "update" | "anonymous" | "Added DE tax rate"   |

  Scenario: Read value revisions with dimensions
    Given "PUT /dimensions/after" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | "ISO 8601"   |
    When "PUT /keys/tax-rate/values?country=DE&after=2007-01-01T00:00:00Z" responds "201 Created" when requested with an array at "/values":
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/values/revisions" responds "200 OK" with an array at "/revisions":
      | /id | /timestamp             | /type    | /user       | /comment |
      | 5   | "2017-07-07T22:09:21Z" | "update" | "anonymous" |          |

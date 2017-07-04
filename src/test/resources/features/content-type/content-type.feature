Feature: Content-Type

  Background: Dimension, key and value
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | "ISO 3166"   |
    And "PUT /keys/tax-rate" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "number"     | ".."         |
    And "PUT /keys/tax-rate/value" responds "201 Created" when requested with:
      | /value |
      | 0.19   |

  Scenario Outline: Response Content-Type
    Then "GET <uri>" responds "200 OK" with headers:
      | Content-Type     |
      | application/json |
    Examples:
      | uri                               |
      | /relations                        |
      | /relations/=                      |
      | /dimensions                       |
      # TODO /dimensions/revisions
      # TODO /dimensions/revisions/{revision}
      | /dimensions/country               |
      | /dimensions/country/revisions     |
      | /dimensions/country/revisions/1   |
      | /keys                             |
      # TODO /keys/revisions
      # TODO /keys/revisions/{revision}
      | /keys/tax-rate                    |
      | /keys/tax-rate/revisions          |
      | /keys/tax-rate/revisions/2        |
      | /keys/tax-rate/value              |
      | /keys/tax-rate/value/revisions    |
      | /keys/tax-rate/value/revisions/3  |
      # TODO | /keys/tax-rate/values/revisions   |
      # TODO | /keys/tax-rate/values/revisions/3 |

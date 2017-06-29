Feature: Dimension revisions

  Background: Updated dimension
    Given "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description               |
      | "string"     | "~"       | "Client Device Identifier" |
    And "DELETE /dimensions/device" responds successfully

  Scenario: Read dimension revisions should paginate
    Then "GET /dimensions/device/revisions?limit=2" responds successfully with:
      | /next/href                                                          |
      | "http://localhost:8080/dimensions/device/revisions?limit=2&after=2" |
    And "GET /dimensions/device/revisions?limit=2&after=2" responds successfully with an array at "/dimensions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /relation | /description |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | "="       | ".."         |
    Then "GET /dimensions/device/revisions?limit=2&after=2" responds successfully with:
      | /next/href |
      |            |

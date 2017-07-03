Feature: Key revisions

  Background: Updated key
    Given "PUT /keys/device" responds successfully when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    And "PUT /keys/device" responds successfully when requested with:
      | /schema/type | /description               |
      | "string"     | "Client Device Identifier" |
    And "DELETE /keys/device" responds successfully

  Scenario: Read key revisions should paginate
    Then "GET /keys/device/revisions?limit=2" responds successfully with:
      | /next/href                                                    |
      | "http://localhost:8080/keys/device/revisions?limit=2&after=2" |
    And "GET /keys/device/revisions?limit=2&after=2" responds successfully with an array at "/revisions":
      | /id      | /revision/id | /revision/type | /revision/user | /revision/comment | /schema/type | /description |
      | "device" | 1            | "create"       | "anonymous"    | ".."              | "string"     | ".."         |
    Then "GET /keys/device/revisions?limit=2&after=2" responds successfully with:
      | /next/href |
      |            |

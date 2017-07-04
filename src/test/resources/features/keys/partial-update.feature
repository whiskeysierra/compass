Feature: Key partial update

  Scenario Outline: Partially updating a key (JSON Merge Patch)
    Given "PUT /keys/version" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    When "PATCH /keys/version" responds "200 OK" when requested with as "<content-type>":
      | /description                 |
      | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/version" responds "200 OK" with:
      | /schema/type | /description                 |
      | "string"     | "Lorem ipsum dolor sit amet" |
    Examples:
      | content-type                 |
      | application/json             |
      | application/merge-patch+json |

  Scenario: Partially updating a key (JSON Patch)
    Given "PUT /keys/version" responds "201 Created" when requested with:
      | /schema/type | /description |
      | "string"     | ".."         |
    When "PATCH /keys/version" responds "200 OK" when requested with an array as "application/json-patch+json":
      | /op       | /path          | /value                       |
      | "replace" | "/description" | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/version" responds "200 OK" with:
      | /schema/type | /description                 |
      | "string"     | "Lorem ipsum dolor sit amet" |

  Scenario: Partially updating a non-existing key fails
    Given "GET /keys/device" responds "404 Not Found"
    Then "PATCH /keys/device" responds "404 Not Found" when requested with:
      | /id      |
      | "device" |

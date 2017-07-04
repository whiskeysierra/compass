Feature: Dimension partial update

  Scenario Outline: Partially updating a dimension (JSON Merge Patch)
    Given "PUT /dimensions/version" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "PATCH /dimensions/version" responds "200 OK" when requested with as "<content-type>":
      | /description                 |
      | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/version" responds "200 OK" with:
      | /schema/type | /relation | /description                 |
      | "string"     | "="       | "Lorem ipsum dolor sit amet" |
    Examples:
      | content-type                 |
      | application/json             |
      | application/merge-patch+json |

  Scenario: Partially updating a dimension (JSON Patch)
    Given "PUT /dimensions/version" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "PATCH /dimensions/version" responds "200 OK" when requested with an array as "application/json-patch+json":
      | /op       | /path          | /value                       |
      | "replace" | "/description" | "Lorem ipsum dolor sit amet" |
    Then "GET /dimensions/version" responds "200 OK" with:
      | /schema/type | /relation | /description                 |
      | "string"     | "="       | "Lorem ipsum dolor sit amet" |

  Scenario: Partially updating a non-existing dimension fails
    Given "GET /dimensions/device" responds "404 Not Found"
    Then "PATCH /dimensions/device" responds "404 Not Found" when requested with:
      | /id      |
      | "device" |

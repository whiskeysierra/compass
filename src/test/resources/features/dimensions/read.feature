Feature: Reading dimension

  Scenario: Get dimension
    Given "PUT /dimensions/device" responds successfully when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    When "GET /dimensions/device" responds "200 OK" with:
      | /id      | /schema/type | /relation | /description |
      | "device" | "string"     | "="       | ".."         |

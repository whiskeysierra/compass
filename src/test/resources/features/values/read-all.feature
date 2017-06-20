Feature: Read all values

  Scenario: Get all values
    Given the following keys:
      | /id   | /schema/type | /relation | /description |
      | "foo" | "string"     | "="       | "."          |
      | "bar" | "string"     | "="       | "."          |
      | "baz" | "string"     | "="       | "."          |
    And the following values for key foo:
      | /value |
      | "foo"  |
    And the following values for key bar:
      | /value |
      | "bar"  |
    And the following values for key baz:
      | /value |
      | "baz"  |
    When "GET /values" returns "200 OK" with a list of /entries/foo/values:
      | /value |
      | "foo"  |
    And "GET /values" returns "200 OK" with a list of /entries/bar/values:
      | /value |
      | "bar"  |
    And "GET /values" returns "200 OK" with a list of /entries/baz/values:
      | /value |
      | "baz"  |

  Scenario: Get all values by key pattern
    Given the following keys:
      | /id   | /schema/type | /relation | /description |
      | "foo" | "string"     | "="       | "."          |
      | "bar" | "string"     | "="       | "."          |
      | "baz" | "string"     | "="       | "."          |
    And the following values for key foo:
      | /value |
      | "foo"  |
    And the following values for key bar:
      | /value |
      | "bar"  |
    And the following values for key baz:
      | /value |
      | "baz"  |
    When "GET /values?q=a" returns "200 OK" with an absent list of /entries/foo/values
    And "GET /values?q=a" returns "200 OK" with a list of /entries/bar/values:
      | /value |
      | "bar"  |
    And "GET /values?q=a" returns "200 OK" with a list of /entries/baz/values:
      | /value |
      | "baz"  |

Feature: Key update

  Scenario: Updating a key
    Given the following keys:
      | id               | schema.type | description |
      | "feature.active" | "boolean"   | ".."        |
    When "PUT /keys/feature.active" is requested with this it returns "200 OK":
      | id               | schema.type | schema.enum          | description                  |
      | "feature.active" | "string"    | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |
    Then "GET /keys/feature.active" returns:
      | id               | schema.type | schema.enum          | description                  |
      | "feature.active" | "string"    | ["mobile","desktop"] | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a key failed due to ID mismatch
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "PUT /keys/tax-rate" is requested with this it returns "400 Bad Request":
      | id    | schema.type | description                  |
      | "bar" | "number"    | "Lorem ipsum dolor sit amet" |

  Scenario: Updating a key failed due to schema violation
    Given the following keys:
      | id         | schema.type | description |
      | "tax-rate" | "number"    | ".."        |
    When "PUT /keys/tax-rate" is requested with this:
      | id         | schema.type | description |
      | "tax-rate" | "any"       | false       |
    Then it returns "400 Bad Request" with a list of violations:
      | field           | message                                                                                                           |
      | "$.description" | "$.description: boolean found, string expected"                                                                   |
      | "$.schema.type" | "$.schema.type: does not have a value in the enumeration [array, boolean, integer, null, number, object, string]" |
      | "$.schema.type" | "$.schema.type: string found, array expected"                                                                     |

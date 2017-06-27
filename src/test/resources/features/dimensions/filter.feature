Feature: Filter dimensions

  Background: Filter dimensions
    Given "PUT /dimensions/{id}" (using /id) always returns "201 Created" when requested individually with:
      | /id        | /schema/type | /relation | /description                       |
      | "after"    | "string"     | ">="      | "Chronologically after, ISO 8601"  |
      | "before"   | "string"     | "<="      | "Chronologically before, ISO 8601" |
      | "device"   | "string"     | "="       | "Client device identifier"         |
      | "email"    | "string"     | "~"       | "Email address regex pattern"      |
      | "language" | "string"     | "^"       | "Display language"                 |
      | "location" | "string"     | "^"       | "User geo location"                |

  Scenario: Filter dimensions by id prefix (case insensitive)
    Then "GET /dimensions?q=DE" returns "200 OK" with a list of /dimensions:
      | /id      | /description               |
      | "device" | "Client device identifier" |

  Scenario: Filter dimensions by id infix
    Then "GET /dimensions?q=cat" returns "200 OK" with a list of /dimensions:
      | /id        | /description        |
      | "location" | "User geo location" |

  Scenario: Filter dimensions by id suffix
    Then "GET /dimensions?q=mail" returns "200 OK" with a list of /dimensions:
      | /id     | /description                  |
      | "email" | "Email address regex pattern" |

  Scenario: Filter dimensions by description prefix (case insensitive)
    Then "GET /dimensions?q=display" returns "200 OK" with a list of /dimensions:
      | /id        | /description       |
      | "language" | "Display language" |

  Scenario: Filter dimensions by description infix
    Then "GET /dimensions?q=logic" returns "200 OK" with a list of /dimensions:
      | /id      | /description                       |
      | "after"  | "Chronologically after, ISO 8601"  |
      | "before" | "Chronologically before, ISO 8601" |

  Scenario: Filter dimensions by description suffix
    Then "GET /dimensions?q=identifier" returns "200 OK" with a list of /dimensions:
      | /id      | /description               |
      | "device" | "Client device identifier" |

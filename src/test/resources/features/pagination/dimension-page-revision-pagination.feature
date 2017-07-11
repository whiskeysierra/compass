Feature: Dimension pagination

  Background: Updated dimensions
    Given "PUT /dimensions/device" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/location" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "^"       | ".."         |
    And "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/language" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    And "PUT /dimensions/age" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "integer"    | "="       | ".."         |

  Scenario: Read dimensions should paginate forward
    Then "GET /dimensions/revisions/5?limit=2" responds "200 OK" with an array at "/dimensions":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /dimensions/revisions/5?limit=2" responds "200 OK" with:
      | /prev | /next                                                     |
      |       | "http://localhost:8080/dimensions/revisions/5?limit=2&_after=country" |
    And "GET /dimensions/revisions/5?limit=2&_after=country" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /dimensions/revisions/5?limit=2&_after=country" responds "200 OK" with:
      | /prev                                                     | /next                                                      |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&_before=device" | "http://localhost:8080/dimensions/revisions/5?limit=2&_after=language" |
    And "GET /dimensions/revisions/5?limit=2&_after=language" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "location" |
    And "GET /dimensions/revisions/5?limit=2&_after=language" responds "200 OK" with:
      | /prev                                                       | /next |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&_before=location" |       |

  Scenario: Read dimension revisions should paginate backward
    Then "GET /dimensions/revisions/5?limit=2&_after=language" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "location" |
    And "GET /dimensions/revisions/5?limit=2&_after=language" responds "200 OK" with:
      | /prev                                                       | /next |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&_before=location" |       |
    And "GET /dimensions/revisions/5?limit=2&_before=location" responds "200 OK" with an array at "/dimensions":
      | /id        |
      | "device"   |
      | "language" |
    And "GET /dimensions/revisions/5?limit=2&_before=location" responds "200 OK" with:
      | /prev                                                       | /next                                                    |
      | "http://localhost:8080/dimensions/revisions/5?limit=2&_before=device" | "http://localhost:8080/dimensions/revisions/5?limit=2&_after=language" |
    And "GET /dimensions/revisions/5?limit=2&_before=device" responds "200 OK" with an array at "/dimensions":
      | /id       |
      | "age"     |
      | "country" |
    And "GET /dimensions/revisions/5?limit=2&_before=device" responds "200 OK" with:
      | /prev | /next                                                     |
      |       | "http://localhost:8080/dimensions/revisions/5?limit=2&_after=country" |

  Scenario: Pagination with conflicting directions should fail
    Then "GET /dimensions/revisions/5?_after=3&_before=1" responds "400 Bad Request"

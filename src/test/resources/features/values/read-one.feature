Feature: Read value

  Background: Income tax configuration
    Given the following dimensions:
      | /id      | /schema/type | /relation | /description |
      | "before" | "string"     | "<"       | "ISO 8601"   |
      | "income" | "number"     | "<="      | ".."         |
    And the following keys:
      | /id          | /schema/type | /description |
      | "income-tax" | "number"     | ".."         |
    And the following values for key income-tax:
      | /dimensions/before     | /dimensions/income | /value |
      | "2017-01-01T00:00:00Z" | 8652               | 0      |
      | "2017-01-01T00:00:00Z" | 53665              | 0.14   |
      | "2017-01-01T00:00:00Z" | 254446             | 0.42   |
      | "2017-01-01T00:00:00Z" |                    | 0.45   |
      | "2018-01-01T00:00:00Z" | 8820               | 0      |
      | "2018-01-01T00:00:00Z" | 54057              | 0.14   |
      | "2018-01-01T00:00:00Z" | 256303             | 0.42   |
      | "2018-01-01T00:00:00Z" |                    | 0.45   |
      |                        |                    | 1      |

  Scenario: Get value without any dimensions
    Given the following keys:
      | /id        | /schema/type | /description |
      | "tax-rate" | "number"     | ".."         |
    And the following values for key tax-rate:
      | /value |
      | 0.19   |
    Then "GET /keys/tax-rate/value" returns "200 OK" with:
      | /value |
      | 0.19   |

  Scenario: Get fallback value
    Then "GET /keys/income-tax/value" returns "200 OK" with:
      | /value |
      | 1      |

  Scenario: Get value with dimensions
    Then "GET /keys/income-tax/value?before=2017-06-22T00:07:23Z&income=82000" returns "200 OK" with:
      | /value |
      | 0.42   |

  Scenario: Canonical value URL
    Then "GET /keys/income-tax/value?before=2017-06-22T00:07:23Z&income=82000" returns "200 OK" with headers:
      | Content-Location                                                                      |
      | http://localhost:8080/keys/income-tax/value?before=2018-01-01T00:00:00Z&income=256303 |

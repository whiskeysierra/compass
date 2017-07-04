Feature: Filter keys

  Background: Filter keys
    Given "PUT /keys/{id}" (using /id) always responds "201 Created" when requested individually with:
      | /id                        | /schema/type | /description                                          |
      | "feature.bundling.active"  | "boolean"    | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "boolean"    | "Split orders into multiple parcels, if beneficial"   |
      | "feature.wishlist.active"  | "boolean"    | "Keep a wishlist of items"                            |
      | "income-tax"               | "number"     | "Personal Income Tax Rate"                            |
      | "tax-rate"                 | "number"     | "Value Added Tax Rate"                                |

  Scenario: Filter keys by id prefix (case insensitive)
    Then "GET /keys?q=Feature" responds "200 OK" with an array at "/keys":
      | /id                        | /description                                          |
      | "feature.bundling.active"  | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "Split orders into multiple parcels, if beneficial"   |
      | "feature.wishlist.active"  | "Keep a wishlist of items"                            |

  Scenario: Filter keys by id infix
    Then "GET /keys?q=tax" responds "200 OK" with an array at "/keys":
      | /id          | /description               |
      | "income-tax" | "Personal Income Tax Rate" |
      | "tax-rate"   | "Value Added Tax Rate"     |

  Scenario: Filter keys by id suffix
    Then "GET /keys?q=active" responds "200 OK" with an array at "/keys":
      | /id                        | /description                                          |
      | "feature.bundling.active"  | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "Split orders into multiple parcels, if beneficial"   |
      | "feature.wishlist.active"  | "Keep a wishlist of items"                            |

  Scenario: Filter keys by description prefix (case insensitive)
    Then "GET /keys?q=value" responds "200 OK" with an array at "/keys":
      | /id        | /description           |
      | "tax-rate" | "Value Added Tax Rate" |

  Scenario: Filter keys by description infix
    Then "GET /keys?q=if" responds "200 OK" with an array at "/keys":
      | /id                        | /description                                          |
      | "feature.bundling.active"  | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "Split orders into multiple parcels, if beneficial"   |

  Scenario: Filter keys by description suffix
    Then "GET /keys?q=rate" responds "200 OK" with an array at "/keys":
      | /id          | /description               |
      | "income-tax" | "Personal Income Tax Rate" |
      | "tax-rate"   | "Value Added Tax Rate"     |

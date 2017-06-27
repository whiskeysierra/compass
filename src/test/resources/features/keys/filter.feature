Feature: Filter keys

  Background: Filter keys
    Given "PUT /keys/{id}" (using /id) always returns "201 Created" when requested individually with:
      | /id                        | /schema/type | /description                                          |
      | "feature.bundling.active"  | "boolean"    | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "boolean"    | "Split orders into multiple parcels, if beneficial"   |
      | "feature.wishlist.active"  | "boolean"    | "Keep a wishlist of items"                            |
      | "income-tax"               | "number"     | "Personal Income Tax Rate"                            |
      | "tax-rate"                 | "number"     | "Value Added Tax Rate"                                |

  Scenario: Filter keys by id prefix (case insensitive)
    Then "GET /keys?q=Feature" returns "200 OK" with a list of /keys:
      | /id                        | /description                                          |
      | "feature.bundling.active"  | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "Split orders into multiple parcels, if beneficial"   |
      | "feature.wishlist.active"  | "Keep a wishlist of items"                            |

  Scenario: Filter keys by id infix
    Then "GET /keys?q=tax" returns "200 OK" with a list of /keys:
      | /id          | /description               |
      | "income-tax" | "Personal Income Tax Rate" |
      | "tax-rate"   | "Value Added Tax Rate"     |

  Scenario: Filter keys by id suffix
    Then "GET /keys?q=active" returns "200 OK" with a list of /keys:
      | /id                        | /description                                          |
      | "feature.bundling.active"  | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "Split orders into multiple parcels, if beneficial"   |
      | "feature.wishlist.active"  | "Keep a wishlist of items"                            |

  Scenario: Filter keys by description prefix (case insensitive)
    Then "GET /keys?q=value" returns "200 OK" with a list of /keys:
      | /id        | /description           |
      | "tax-rate" | "Value Added Tax Rate" |

  Scenario: Filter keys by description infix
    Then "GET /keys?q=if" returns "200 OK" with a list of /keys:
      | /id                        | /description                                          |
      | "feature.bundling.active"  | "Bundle orders together in one parcel, if beneficial" |
      | "feature.splitting.active" | "Split orders into multiple parcels, if beneficial"   |

  Scenario: Filter keys by description suffix
    Then "GET /keys?q=rate" returns "200 OK" with a list of /keys:
      | /id          | /description               |
      | "income-tax" | "Personal Income Tax Rate" |
      | "tax-rate"   | "Value Added Tax Rate"     |

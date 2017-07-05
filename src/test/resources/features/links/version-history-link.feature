Feature: Version history link

  Scenario: Dimension exposes version history link
    Given "PUT /dimensions/country" responds "201 Created" when requested with:
      | /schema/type | /relation | /description |
      | "string"     | "="       | ".."         |
    # TODO implement
    #Then "GET /dimensions/country" responds "200 OK" with:
    #  | /version-history                                   |
    #  | http://localhost:8080/dimensions/country/revisions |

 # TODO should revisions only include the latest-version link if it's not deleted right now?

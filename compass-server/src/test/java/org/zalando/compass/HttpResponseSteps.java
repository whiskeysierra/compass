package org.zalando.compass;

import cucumber.api.java.en.Then;
import org.springframework.stereotype.Component;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

@Component
@ScenarioScoped
public class HttpResponseSteps {

    @Then("^the following (.*) are returned:$")
    public void the_following_are_returned(final String key, final Datatable table) {
    }

}

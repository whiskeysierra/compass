package org.zalando.compass;

import cucumber.api.java.en.When;
import org.springframework.stereotype.Component;
import org.zuchini.spring.ScenarioScoped;

@Component
@ScenarioScoped
public class HttpRequestSteps {

    @When("^\"(.*) (.*)\" is requested$")
    public void is_requested(final String method, final String uri) {

    }

}

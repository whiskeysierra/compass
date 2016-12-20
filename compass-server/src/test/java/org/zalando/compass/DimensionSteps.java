package org.zalando.compass;

import cucumber.api.java.en.Given;
import org.springframework.stereotype.Component;
import org.zuchini.spring.ScenarioScoped;

@Component
@ScenarioScoped
public class DimensionSteps {

    @Given("^the default dimensions")
    public void the_default_dimensions() {

    }

}

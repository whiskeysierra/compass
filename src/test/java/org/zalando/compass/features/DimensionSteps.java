package org.zalando.compass.features;

import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Rest;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

import java.io.IOException;

import static com.google.common.collect.ImmutableMap.of;
import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Route.pass;

@Component
@ScenarioScoped
public class DimensionSteps {

    private final Rest rest;
    private final TableMapper mapper;

    @Autowired
    public DimensionSteps(final Rest rest, final TableMapper mapper) {
        this.rest = rest;
        this.mapper = mapper;
    }

    @Given("^there are no dimensions$")
    public void thereAreNoDimensions() {
        // nothing to do
    }

    @Given("^the following dimensions:$")
    public void theFollowingDimensions(final Datatable table) throws IOException {
        rest.put("/dimensions")
                .body(of("dimensions", mapper.map(table)))
                .dispatch(series(),
                        on(SUCCESSFUL).call(pass())).join();
    }

}

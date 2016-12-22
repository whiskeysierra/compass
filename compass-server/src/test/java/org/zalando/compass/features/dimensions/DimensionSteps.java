package org.zalando.compass.features.dimensions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Rest;
import org.zuchini.spring.ScenarioScoped;

import static com.google.common.collect.ImmutableMap.of;
import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Route.pass;

@Component
@ScenarioScoped
public class DimensionSteps {

    private final Rest rest;

    @Autowired
    public DimensionSteps(final Rest rest) {
        this.rest = rest;
    }

    @Given("^the default dimensions")
    public void the_default_dimensions() {
        rest.put("/dimensions")
                .body(of("dimensions", ImmutableList.of(
                        dimension("device", of("type", "string"), "="),
                        dimension("language", of("type", "string", "format", "bcp47"), "^"),
                        dimension("location", of("type", "string", "format", "geohash"), "^"),
                        dimension("before", of("type", "string", "format", "date-time"), "<="),
                        dimension("after", of("type", "string", "format", "date-time"), ">="),
                        dimension("email", of("type", "string", "format", "email"), "~"))))
                .dispatch(series(),
                        on(SUCCESSFUL).call(pass())).join();
    }

    private ImmutableMap<String, Object> dimension(final String id, final ImmutableMap<String, String> schema,
            final String relation) {
        return of(
                "id", id,
                "schema", schema,
                "relation", relation,
                "description", ".."
        );
    }

    @Given("^there are no dimensions$")
    public void there_are_no_dimensions() {
        // nothing to do
    }

}

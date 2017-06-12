package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Rest;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Route.pass;

@Component
@ScenarioScoped
public class ValueSteps {

    private final Rest rest;
    private final TableMapper mapper;

    @Autowired
    public ValueSteps(final Rest rest, final TableMapper mapper) {
        this.rest = rest;
        this.mapper = mapper;
    }

    @Given("^there are no values")
    public void thereAreNoValues() {
        // nothing to do
    }

    @Given("^the following values for key (.+):$")
    public void theFollowingValues(final String key, final Datatable table) throws IOException {
        mapper.map(table).stream()
                .map(node -> rest.put("/keys/{id}/value", key)
                        .queryParams(getDimensions(node))
                        .body(node)
                        .dispatch(series(),
                                on(SUCCESSFUL).call(pass())))
                .forEach(CompletableFuture::join);
    }

    private Multimap<String, String> getDimensions(final JsonNode node) {
        final ImmutableMultimap.Builder<String, String> dimensions = ImmutableMultimap.builder();

        node.path("dimensions").fields().forEachRemaining(e -> {
            dimensions.put(e.getKey(), e.getValue().asText());
        });

        return dimensions.build();
    }

}

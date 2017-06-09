package org.zalando.compass.features;

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
public class KeySteps {

    private final Rest rest;
    private final TableMapper mapper;

    @Autowired
    public KeySteps(final Rest rest, final TableMapper mapper) {
        this.rest = rest;
        this.mapper = mapper;
    }

    @Given("^there are no keys")
    public void thereAreNoKeys() {
        // nothing to do
    }

    @Given("^the following keys:$")
    public void theFollowingKeys(final Datatable table) throws IOException {
        mapper.map(table).stream()
                .map(key -> rest.put("/keys/{id}", key.get("id").asText())
                        .body(key)
                        .dispatch(series(),
                                on(SUCCESSFUL).call(pass())))
                .forEach(CompletableFuture::join);
    }

}

package org.zalando.compass.features.hooks;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Rest;
import org.zalando.tracer.aspectj.Traced;

import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Route.pass;

@Component
public class Reset {

    private final Rest rest;

    @Autowired
    public Reset(final Rest rest) {
        this.rest = rest;
    }

    @Traced
    @Before
    public void begin() {
        delete("keys");
        delete("dimensions");
    }

    private void delete(final String resource) {
        rest.get("/{path}", resource).dispatch(series(),
                on(SUCCESSFUL).call(JsonNode.class, root ->
                        StreamSupport.stream(root.get(resource).spliterator(), false)
                                .map(node -> delete(resource, node.get("id").asText()))
                                .forEach(CompletableFuture::join))).join();
    }

    private CompletableFuture<Void> delete(final String resource, final String id) {
        return rest.delete("/{path}/{id}", resource, id).dispatch(series(),
                on(SUCCESSFUL).call(pass()));
    }

}

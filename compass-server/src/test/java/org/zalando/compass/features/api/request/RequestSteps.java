package org.zalando.compass.features.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.zalando.compass.features.Shared;
import org.zalando.riptide.Requester;
import org.zalando.riptide.Rest;
import org.zuchini.spring.ScenarioScoped;

import java.util.function.Function;

import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.riptide.Bindings.anySeries;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Route.responseEntityOf;

@Component
@ScenarioScoped
public class RequestSteps {

    private final Rest rest;
    private final Shared shared;

    @Autowired
    public RequestSteps(final Rest rest, final Shared shared) {
        this.rest = rest;
        this.shared = shared;
    }

    @When("^\"(.+) (.+)\" is requested$")
    public void is_requested(final HttpMethod method, final String uri) {
        select(method).apply(uri)
                .dispatch(series(),
                        on(SUCCESSFUL).call(responseEntityOf(JsonNode.class), shared::setResponse),
                        anySeries().call(responseEntityOf(Object.class), shared::setResponse)).join();
    }

    private Function<String, Requester> select(final HttpMethod method) {
        switch (method){
            case GET:
                return rest::get;
            case HEAD:
                return rest::head;
            case POST:
                return rest::post;
            case PUT:
                return rest::put;
            case PATCH:
                return rest::patch;
            case DELETE:
                return rest::delete;
            case OPTIONS:
                return rest::options;
            case TRACE:
                return rest::trace;
            default:
                throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

}

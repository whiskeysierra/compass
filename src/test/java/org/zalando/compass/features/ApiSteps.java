package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Requester;
import org.zalando.riptide.Rest;
import org.zalando.riptide.capture.Capture;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.riptide.Bindings.anySeries;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Navigators.statusCode;
import static org.zalando.riptide.Route.responseEntityOf;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

@Component
@ScenarioScoped
public class ApiSteps {

    private final ThreadLocal<ResponseEntity<JsonNode>> lastResponse = new ThreadLocal<>();

    private final TableMapper mapper;
    private final Rest rest;

    @Autowired
    public ApiSteps(final TableMapper mapper, final Rest rest) {
        this.mapper = mapper;
        this.rest = rest;
    }

    @Given("^\"(.+) (.*)\" returns \"(\\d+) (.+)\"$")
    public void returns(final HttpMethod method, final String uri, final int statusCode, final String reasonPhrase)
            throws IOException {

        final Capture<ClientHttpResponse> capture = Capture.empty();
        final ClientHttpResponse response = select(method).apply(uri)
                .dispatch(series(),
                        anySeries().call(ClientHttpResponse.class, capture))
                .thenApply(capture)
                .join();

        assertThat(response.getRawStatusCode(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    @Then("^\"(.+) (.*)\" returns:$")
    public void returns(final HttpMethod method, final String uri, final Datatable expected) throws IOException {
        final JsonNode row = request(method, uri).getBody();
        final List<String> headers = expected.getHeader();
        final Datatable actual = mapper.map(singletonList(row), headers);

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(.+) (.*)\" returns a list of (.+):$")
    public void returnsListOf(final HttpMethod method, final String uri, final String key, final Datatable expected) throws IOException {
        final JsonNode body = request(method, uri).getBody();

        final ArrayList<JsonNode> nodes = newArrayList(body.get(key));
        final Datatable actual = mapper.map(nodes, expected.getHeader());

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(.+) (.*)\" returns an empty list of (.+)$")
    public void no_are_returned(final HttpMethod method, final String uri, final String key) {
        final JsonNode list = request(method, uri).getBody().get(key);
        assertThat(list.size(), is(0));
    }

    private ResponseEntity<JsonNode> request(final HttpMethod method, final String uri) {
        final Capture<ResponseEntity<JsonNode>> capture = Capture.empty();

        return select(method).apply(uri)
                .dispatch(series(),
                        on(SUCCESSFUL).call(responseEntityOf(JsonNode.class), capture))
                .thenApply(capture)
                .join();
    }

    @When("^\"(.+) (.*)\" is requested with this it returns \"(\\d+) (.+)\":$")
    public void isRequestedWithThisItReturns(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final Datatable table) throws IOException {

        final Capture<ClientHttpResponse> capture = Capture.empty();
        final ClientHttpResponse response = select(method).apply(uri)
                .body(mapper.map(table).get(0))
                .dispatch(statusCode(),
                        on(statusCode).call(ClientHttpResponse.class, capture))
                .thenApply(capture)
                .join();

        assertThat(response.getRawStatusCode(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    @When("^\"(.+) (.*)\" is requested with this:$")
    public void isRequestedWithThisItReturns(final HttpMethod method, final String uri, final Datatable table)
            throws IOException {

        final Capture<ResponseEntity<JsonNode>> capture = Capture.empty();
        final ResponseEntity<JsonNode> response = select(method).apply(uri)
                .body(mapper.map(table).get(0))
                .dispatch(series(),
                        anySeries().call(responseEntityOf(JsonNode.class), capture))
                .thenApply(capture)
                .join();

        lastResponse.set(response);
    }

    @Then("^it returns \"(\\d+) (.+)\" with a list of (.+):$")
    public void itReturnsWithAListOf(final int statusCode, final String reasonPhrase, final String key,
            final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = lastResponse.get();

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));

        final ArrayList<JsonNode> nodes = newArrayList(response.getBody().get(key));
        final Datatable actual = mapper.map(nodes, expected.getHeader());

        assertThat(actual, matchesTable(expected));
    }

    // TODO ues dynamic execute version
    private Function<String, Requester> select(final HttpMethod method) {
        switch (method) {
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

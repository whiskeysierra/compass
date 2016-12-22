package org.zalando.compass.features.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;
import static org.zalando.fauxpas.FauxPas.throwingBiFunction;
import static org.zalando.riptide.Bindings.anySeries;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.series;
import static org.zalando.riptide.Navigators.statusCode;
import static org.zalando.riptide.Route.responseEntityOf;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

@Component
@ScenarioScoped
public class Steps {

    private final ThreadLocal<ResponseEntity<JsonNode>> lastResponse = new ThreadLocal<>();

    private final ObjectMapper mapper;
    private final Rest rest;

    @Autowired
    public Steps(final ObjectMapper mapper, final Rest rest) {
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
    public void returns(final HttpMethod method, final String uri, final Datatable expected) {
        final ResponseEntity<JsonNode> response = request(method, uri);

        final List<String> headers = expected.getHeader();
        final Map<String, String> row = toRow(headers, response.getBody());

        final Datatable actual = Datatable.fromMaps(singletonList(row), headers);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(.+) (.*)\" returns a list of (.+):$")
    public void returnsListOf(final HttpMethod method, final String uri, final String key, final Datatable expected) {
        final JsonNode body = request(method, uri).getBody();

        matchesListOf(expected, body, key);
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

        final Map<String, Object> body = new HashMap<>(table.toMap().get(0));

        body.replaceAll(throwingBiFunction((key, schema) ->
                mapper.readTree(schema.toString())));

        final Capture<ClientHttpResponse> capture = Capture.empty();
        final ClientHttpResponse response = select(method).apply(uri)
                .body(body)
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

        final Map<String, Object> body = new HashMap<>(table.toMap().get(0));

        body.replaceAll(throwingBiFunction((key, schema) ->
                mapper.readTree(schema.toString())));

        final Capture<ResponseEntity<JsonNode>> capture = Capture.empty();
        final ResponseEntity<JsonNode> response = select(method).apply(uri)
                .body(body)
                .dispatch(series(),
                        anySeries().call(responseEntityOf(JsonNode.class), capture))
                .thenApply(capture)
                .join();

        lastResponse.set(response);
    }

    @Then("^it returns \"(\\d+) (.+)\" with a list of (.+):$")
    public void itReturnsWithAListOf( final int statusCode, final String reasonPhrase, final String key,
            final Datatable expected) {

        final ResponseEntity<JsonNode> response = lastResponse.get();

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));

        matchesListOf(expected, response.getBody(), key);
    }

    private void matchesListOf(final Datatable expected, final JsonNode body, final String key) {
        final JsonNode list = body.get(key);
        final List<Map<String, String>> rows = new ArrayList<>(list.size());
        final List<String> headers = expected.getHeader();
        final Iterator<JsonNode> elements = list.elements();

        while (elements.hasNext()) {
            rows.add(toRow(headers, elements.next()));
        }

        final Datatable actual = Datatable.fromMaps(rows, headers);
        assertThat(actual, matchesTable(expected));
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

    private Map<String, String> toRow(final List<String> headers, final JsonNode element) {
        final Map<String, String> row = new HashMap<>(headers.size());

        headers.forEach(header -> {
            @Nullable final JsonNode node = element.get(header);

            if (node == null) {
                throw new NullPointerException(element + " doesn't contain property " + header);
            }

            row.put(header, node.toString());
        });

        return row;
    }

}

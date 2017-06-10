package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Rest;
import org.zalando.riptide.Route;
import org.zalando.riptide.capture.Capture;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;
import static org.zalando.riptide.Bindings.anyStatus;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.contentType;
import static org.zalando.riptide.Navigators.status;
import static org.zalando.riptide.Route.call;
import static org.zalando.riptide.Route.responseEntityOf;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

// TODO extract common parts into small private methods
@Component
@ScenarioScoped
public class ApiSteps {

    private final ThreadLocal<CompletableFuture<ResponseEntity<JsonNode>>> lastResponse = new ThreadLocal<>();

    private final TableMapper mapper;
    private final Rest rest;

    @Autowired
    public ApiSteps(final TableMapper mapper, final Rest rest) {
        this.mapper = mapper;
        this.rest = rest;
    }

    @Given("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\"$")
    public void returns(final HttpMethod method, final String uri, final int statusCode, final String reasonPhrase)
            throws IOException {

        final ResponseEntity<JsonNode> response = request(method, uri);

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    @Given("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with headers:$")
    public void returnsWithHeaders(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = request(method, uri);

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));

        final Map<String, String> headers = response.getHeaders().toSingleValueMap();
        final Datatable actual = Datatable.fromMaps(singletonList(headers), expected.getHeader());

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with:$")
    public void returnsWith(final HttpMethod method, final String uri, final int statusCode, final String reasonPhrase, 
            final Datatable expected) throws IOException {
        final ResponseEntity<JsonNode> response = request(method, uri);

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
        
        final JsonNode body = response.getBody();
        final List<String> headers = expected.getHeader();
        final Datatable actual = mapper.map(singletonList(body), headers);

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with a list of (.+):$")
    public void returnWithAListOf(final HttpMethod method, final String uri, final int statusCode, 
            final String reasonPhrase, final String key, final Datatable expected) throws IOException {
        final ResponseEntity<JsonNode> response = request(method, uri);

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
        
        final JsonNode body = response.getBody();
        final ArrayList<JsonNode> nodes = newArrayList(body.get(key));
        final Datatable actual = mapper.map(nodes, expected.getHeader());

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with an empty list of (.+)$")
    public void returnsWithAnEmptyListOf(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final String key) {
        final ResponseEntity<JsonNode> response = request(method, uri);

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
        
        final JsonNode list = response.getBody().get(key);
        assertThat(list.size(), is(0));
    }

    @When("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" when requested with:$")
    public void returnsWhenRequestedWith(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final Datatable table) throws IOException {

        final ResponseEntity<JsonNode> response = request(method, uri, mapper.map(table).get(0));

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    @When("^\"([A-Z]+) ([^ ]*)\" when requested with:$")
    public void whenRequestedWith(final HttpMethod method, final String uri, final Datatable table)
            throws IOException {

        lastResponse.set(requestAsync(method, uri, mapper.map(table).get(0)));
    }

    @Then("^\"(\\d+) (.+)\" was returned with a list of (.+):$")
    public void wasReturnedWithAListOf(final int statusCode, final String reasonPhrase, final String key,
            final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = lastResponse.get().join();

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));

        final ArrayList<JsonNode> nodes = newArrayList(response.getBody().get(key));
        final Datatable actual = mapper.map(nodes, expected.getHeader());

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(\\d+) (.+)\" was returned with headers:$")
    public void wasReturnedWithHeaders(final int statusCode, final String reasonPhrase, final Datatable expected) {

        final ResponseEntity<JsonNode> response = lastResponse.get().join();

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));

        final Map<String, String> headers = response.getHeaders().toSingleValueMap();
        final Datatable actual = Datatable.fromMaps(singletonList(headers), expected.getHeader());

        assertThat(actual, matchesTable(expected));
    }

    private ResponseEntity<JsonNode> request(final HttpMethod method, final String uri) {
        return request(method, uri, null);
    }

    private ResponseEntity<JsonNode> request(final HttpMethod method, final String uri,
            final Object body) {

        return requestAsync(method, uri, body).join();
    }

    private CompletableFuture<ResponseEntity<JsonNode>> requestAsync(final HttpMethod method, final String uri,
            final Object body) {

        final Capture<ResponseEntity<JsonNode>> capture = Capture.empty();
        final Route route = call(responseEntityOf(JsonNode.class), capture);

        return rest.execute(method, uri)
                .body(body)
                .dispatch(status(),
                        on(HttpStatus.NO_CONTENT).call(route),
                        anyStatus().dispatch(contentType(),
                                on(APPLICATION_JSON).call(route),
                                on(parseMediaType("application/*+json")).call(route)))
                .thenApply(capture);
    }

}

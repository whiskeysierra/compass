package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;
import static org.zalando.compass.library.JsonMutator.setAt;
import static org.zalando.riptide.Bindings.anyStatus;
import static org.zalando.riptide.Bindings.on;
import static org.zalando.riptide.Navigators.contentType;
import static org.zalando.riptide.Navigators.status;
import static org.zalando.riptide.Route.call;
import static org.zalando.riptide.Route.responseEntityOf;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

@Component
@ScenarioScoped
public class HttpSteps {

    private final ThreadLocal<CompletableFuture<ResponseEntity<JsonNode>>> lastResponse = new ThreadLocal<>();

    private final TableMapper mapper;
    private final Rest rest;

    @Autowired
    public HttpSteps(final TableMapper mapper, final Rest rest) {
        this.mapper = mapper;
        this.rest = rest;
    }

    @Given("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\"$")
    public void returns(final HttpMethod method, final String uri, final int statusCode, final String reasonPhrase)
            throws IOException {

        verifyStatus(request(method, uri), statusCode, reasonPhrase);
    }

    @Given("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with headers:$")
    public void returnsWithHeaders(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = request(method, uri);
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = renderHeaders(response, expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with:$")
    public void returnsWith(final HttpMethod method, final String uri, final int statusCode, final String reasonPhrase,
            final Datatable expected) throws IOException {
        final ResponseEntity<JsonNode> response = request(method, uri);
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = renderBody(response, expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with a list of (.+):$")
    public void returnWithAListOf(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final String path, final Datatable expected) throws IOException {
        final ResponseEntity<JsonNode> response = request(method, uri);
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = renderList(response, path, expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" with an (absent|empty) list of (.+)$")
    public void returnsWithAnAbsentOrEmptyListOf(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final String type, final String key) {
        final ResponseEntity<JsonNode> response = request(method, uri);

        verifyStatus(response, statusCode, reasonPhrase);

        final JsonNode list = response.getBody().at(key);

        if (type.equals("empty")) {
            assertThat(list, is(not(instanceOf(MissingNode.class))));
        }

        assertThat(list.size(), is(0));
    }

    @When("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" when requested with:$")
    public void returnsWhenRequestedWith(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final Datatable table) throws IOException {

        final ResponseEntity<JsonNode> response = request(method, uri, mapper.map(table).get(0));

        verifyStatus(response, statusCode, reasonPhrase);
    }

    @When("^\"([A-Z]+) ([^ ]*)\" returns \"(\\d+) (.+)\" when requested with a list of (.+):$")
    public void returnsWhenRequestedWithAListOf(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final String path, final Datatable table) throws IOException {

        final ObjectNode body = new ObjectNode(instance);
        setAt(body, path, new ArrayNode(instance).addAll(mapper.map(table)));

        final ResponseEntity<JsonNode> response = request(method, uri, body);

        verifyStatus(response, statusCode, reasonPhrase);
    }

    @When("^\"([A-Z]+) ([^ ]*)\" when requested with:$")
    public void whenRequestedWith(final HttpMethod method, final String uri, final Datatable table)
            throws IOException {

        lastResponse.set(requestAsync(method, uri, mapper.map(table).get(0)));
    }

    @When("^\"([A-Z]+) ([^ ]*)\" when requested with a list of (.+):$")
    public void whenRequestedWithAListOf(final HttpMethod method, final String uri, final String path,
            final Datatable table) throws IOException {

        final ObjectNode body = new ObjectNode(instance);
        setAt(body, path, new ArrayNode(instance).addAll(mapper.map(table)));

        lastResponse.set(requestAsync(method, uri, body));
    }

    @Then("^\"(\\d+) (.+)\" was returned with a list of (.+):$")
    public void wasReturnedWithAListOf(final int statusCode, final String reasonPhrase, final String path,
            final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = lastResponse.get().join();
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = renderList(response, path, expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(\\d+) (.+)\" was returned with:$")
    public void wasReturnedWit(final int statusCode, final String reasonPhrase, final Datatable expected) throws IOException {
        final ResponseEntity<JsonNode> response = lastResponse.get().join();

        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = renderBody(response, expected);

        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(\\d+) (.+)\" was returned with headers:$")
    public void wasReturnedWithHeaders(final int statusCode, final String reasonPhrase, final Datatable expected) {
        final ResponseEntity<JsonNode> response = lastResponse.get().join();

        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = renderHeaders(response, expected);

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

    private void verifyStatus(final ResponseEntity<JsonNode> response, final int statusCode, final String reasonPhrase) {
        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    private Datatable renderHeaders(final ResponseEntity<JsonNode> response, final Datatable expected) {
        final Map<String, String> headers = response.getHeaders().toSingleValueMap();
        return Datatable.fromMaps(singletonList(headers), expected.getHeader());
    }

    private Datatable renderBody(final ResponseEntity<JsonNode> response, final Datatable expected) throws IOException {
        final JsonNode body = response.getBody();
        final List<String> headers = expected.getHeader();
        return mapper.map(singletonList(body), headers);
    }

    private Datatable renderList(final ResponseEntity<JsonNode> response, final String path, final Datatable expected) throws IOException {
        final JsonNode body = response.getBody();
        final ArrayList<JsonNode> nodes = newArrayList(body.at(path));
        return mapper.map(nodes, expected.getHeader());
    }

}

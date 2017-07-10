package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.ImmutableList;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zalando.riptide.Requester;
import org.zalando.riptide.Rest;
import org.zalando.riptide.Route;
import org.zalando.riptide.capture.Capture;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;
import static org.zalando.compass.library.JsonMutator.withAt;
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

    private CompletableFuture<ResponseEntity<JsonNode>> lastResponse;

    private final TableMapper mapper;
    private final Rest rest;

    @Autowired
    public HttpSteps(final TableMapper mapper, final Rest rest) {
        this.mapper = mapper;
        this.rest = rest;
    }

    @Given("^\"([A-Z]+) ([^ ]*)\"(?: and \"([^:]+): ([^\"]+)\")? responds \"(\\d+) ([^\"]+)\"$")
    public void responds(final HttpMethod method, final String uri, @Nullable final String headerName,
            @Nullable final String headerValue, final int statusCode, final String reasonPhrase) throws IOException {

        final Requester requester = rest.execute(method, uri);

        if (headerName != null && headerValue != null) {
            requester.header(headerName, headerValue);
        }

        final ResponseEntity<JsonNode> response = requestAsync(requester, null).join();
        verifyStatus(response, statusCode, reasonPhrase);
    }

    @Given("^\"([A-Z]+) ([^ ]*)\" responds \"(\\d+) ([^\"]+)\" with headers:$")
    public void respondsWithHeaders(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = requestAsync(method, uri).join();
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = render(response.getHeaders(), expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" responds \"(\\d+) ([^\"]+)\" with(?: an array)?(?: at \"(.+)\")?:$")
    public void repsondsWithAnArrayAt(final HttpMethod method, final String uri, final int statusCode,
            final String reasonPhrase, @Nullable final String path, final Datatable expected)
            throws IOException {

        final ResponseEntity<JsonNode> response = requestAsync(method, uri).join();
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = render(response, path, expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"([A-Z]+) ([^ ]*)\" responds \"(\\d+) ([^\"]+)\" with an empty array(?: at \"(.+)\")?$")
    public void respondsWithAnEmptyArrayAt(final HttpMethod method, final String uri,
            final int statusCode, final String reasonPhrase, @Nullable final String path) {
        final ResponseEntity<JsonNode> response = requestAsync(method, uri).join();

        verifyStatus(response, statusCode, reasonPhrase);

        final JsonNode body = response.getBody();
        final JsonNode array = resolve(body, path);

        assertThat(array, is(not(instanceOf(MissingNode.class))));
        assertThat(array.size(), is(0));
    }

    @When("^\"([A-Z]+) ([^ ]*)\" \\(using (.+)\\) always responds \"(\\d+) ([^\"]+)\" when requested individually with:$")
    public void usingAlwaysReturnsWhenRequestedIndividuallyWith(final HttpMethod method, final String uriTemplate,
            final String path, final int statusCode, final String reasonPhrase,
            final Datatable table) throws IOException {

        mapper.map(table).stream()
                .map(node -> requestAsync(rest.execute(method, uriTemplate, node.at(path).asText()), node))
                .map(CompletableFuture::join)
                .forEach(response -> verifyStatus(response, statusCode, reasonPhrase));
    }

    @When("^\"([A-Z]+) ([^ ]*)\"(?: and \"([^:]+): ([^\"]+)\")? (?:(responds) \"(\\d+) ([^\"]+)\" )?when requested with(?: an (array))?(?: at \"(.*)\")?(?: as \"(.+)\")?:$")
    public void respondsWhenRequestedWithAnArrayAtAs(
            final HttpMethod method, final String uri,
            @Nullable final String headerName, @Nullable final String headerValue,
            @Nullable final String responds,
            final String statusCode,
            @Nullable final String reasonPhrase,
            @Nullable final String type,
            @Nullable final String path,
            @Nullable final String contentType, final Datatable table) throws IOException {

        final JsonNode body = produceBody(type, path, table);

        final Requester requester = rest.execute(method, uri);

        if (headerName != null && headerValue != null) {
            requester.header(headerName, headerValue);
        }

        if (contentType != null) {
            requester.contentType(MediaType.parseMediaType(contentType));
        }

        final CompletableFuture<ResponseEntity<JsonNode>> future = requestAsync(requester, body);

        if (responds == null) {
            lastResponse = future;
        } else {
            final ResponseEntity<JsonNode> response = future.join();
            verifyStatus(response, Integer.parseInt(statusCode), reasonPhrase);
        }
    }

    private JsonNode produceBody(@Nullable final String type, @Nullable final String path, final Datatable table) throws IOException {
        final List<JsonNode> nodes = mapper.map(table);

        final JsonNode array = "array".equals(type) ?
                new ArrayNode(instance).addAll(nodes) :
                getOnlyElement(nodes);

        if (path == null || path.isEmpty()) {
            return array;
        } else {
            return withAt(path, array);
        }
    }

    @Then("^\"(\\d+) ([^\"]+)\" was responded with(?:(?: an array)? at \"(.+)\")?:$")
    public void wasRespondedWithAnArrayAt(final int statusCode, final String reasonPhrase,
            @Nullable final String path, final Datatable expected) throws IOException {

        final ResponseEntity<JsonNode> response = lastResponse.join();
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = render(response, path, expected);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^\"(\\d+) ([^\"]+)\" was responded with headers:$")
    public void wasReturnedWithHeaders(final int statusCode, final String reasonPhrase,
            final Datatable expected) {

        final ResponseEntity<JsonNode> response = lastResponse.join();
        verifyStatus(response, statusCode, reasonPhrase);

        final Datatable actual = render(response.getHeaders(), expected);
        assertThat(actual, matchesTable(expected));
    }

    @CheckReturnValue
    private CompletableFuture<ResponseEntity<JsonNode>> requestAsync(final HttpMethod method, final String uri) {
        return requestAsync(rest.execute(method, uri), null);
    }

    @CheckReturnValue
    private CompletableFuture<ResponseEntity<JsonNode>> requestAsync(final Requester requester, final Object body) {
        final Capture<ResponseEntity<JsonNode>> capture = Capture.empty();
        final Route route = call(responseEntityOf(JsonNode.class), capture);

        return requester
                .body(body)
                .dispatch(status(),
                        on(HttpStatus.NO_CONTENT).call(route),
                        anyStatus().dispatch(contentType(),
                                on(APPLICATION_JSON).call(route),
                                on(parseMediaType("application/*+json")).call(route)))
                .thenApply(capture);
    }

    private void verifyStatus(final ResponseEntity<JsonNode> response, final int statusCode,
            final String reasonPhrase) {

        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    private Datatable render(final HttpHeaders headers, final Datatable expected) {
        final List<Map<String, String>> rows = singletonList(headers.toSingleValueMap());
        return Datatable.fromMaps(rows, expected.getHeader());
    }

    private Datatable render(final ResponseEntity<JsonNode> response, @Nullable final String path,
            final Datatable expected) throws IOException {
        final JsonNode body = response.getBody();
        final JsonNode node = resolve(body, path);

        final List<JsonNode> nodes = node.isArray() ? ImmutableList.copyOf(node) : singletonList(node);
        return mapper.map(nodes, expected.getHeader());
    }

    private JsonNode resolve(final JsonNode body, @Nullable final String path) {
        return path == null ? body : body.at(path);
    }

}

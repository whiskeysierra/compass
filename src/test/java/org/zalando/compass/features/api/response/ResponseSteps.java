package org.zalando.compass.features.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zalando.compass.features.Shared;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.runner.tables.DatatableMatchers;
import org.zuchini.spring.ScenarioScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

@Component
@ScenarioScoped
public class ResponseSteps {

    private final Shared shared;

    @Autowired
    public ResponseSteps(final Shared shared) {
        this.shared = shared;
    }

    @Then("^\"(\\d+) (.+)\" is returned$")
    public void is_returned(final int statusCode, final String reasonPhrase) {
        final ResponseEntity<?> response = shared.getResponse();
        assertThat(response.getStatusCodeValue(), is(statusCode));
        assertThat(response.getStatusCode().getReasonPhrase(), is(reasonPhrase));
    }

    @Then("^the following is returned:$")
    public void the_following_is_returned(final Datatable expected) {
        final List<String> headers = expected.getHeader();
        final Map<String, String> row = toRow(headers, shared.getBodyAs(JsonNode.class));

        final Datatable actual = Datatable.fromMaps(Collections.singletonList(row), headers);
        assertThat(actual, matchesTable(expected));
    }

    @Then("^the following (.+) are returned:$")
    public void the_following_are_returned(final String key, final Datatable expected) {
        final JsonNode list = shared.getBodyAs(JsonNode.class).get(key);

        final List<Map<String, String>> rows = new ArrayList<>(list.size());
        final List<String> headers = expected.getHeader();
        final Iterator<JsonNode> elements = list.elements();

        while (elements.hasNext()) {
            rows.add(toRow(headers, elements.next()));
        }

        final Datatable actual = Datatable.fromMaps(rows, headers);
        assertThat(actual, matchesTable(expected));
    }

    private Map<String, String> toRow(final List<String> headers, final JsonNode element) {
        final Map<String, String> row = new HashMap<>(headers.size());

        headers.forEach(header ->
                row.put(header, toString(element.get(header))));

        return row;
    }

    private String toString(final JsonNode node) {
        return node instanceof ContainerNode ? node.toString() : node.asText();
    }

    @Then("^no (.+) are returned:$")
    public void no_are_returned(final String key) {
        final JsonNode list = shared.getBodyAs(JsonNode.class).get(key);
        assertThat(list.size(), is(0));
    }

}

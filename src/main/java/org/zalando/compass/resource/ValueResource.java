package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Entries;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValuePage;
import org.zalando.compass.library.FilterParser;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

// TODO drop special parameter from filter map
// TODO find a way to transform filter map into a "typed" version, e.g. Map<String, JsonNode>
@RestController
public class ValueResource {

    private final FilterParser parser;
    private final JsonReader reader;
    private final ValueService service;

    @Autowired
    public ValueResource(final FilterParser parser, final JsonReader reader,
            final ValueService service) {
        this.parser = parser;
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/keys/{id}/value")
    public Value get(@PathVariable final String id, @RequestParam final Map<String, String> query) throws IOException {
        final Map<String, JsonNode> filter = parser.parse(query);
        return service.read(id, filter);
    }

    @RequestMapping(method = GET, path = "/keys/{id}/values")
    public ValuePage getAll(@PathVariable final String id, @RequestParam final Map<String, String> query) throws IOException {
        final Map<String, JsonNode> filter = parser.parse(query);
        return new ValuePage(service.readAllByKey(id, filter));
    }

    @RequestMapping(method = POST, path = "/keys/{id}/values")
    public ResponseEntity<ValuePage> post(@PathVariable final String id, @RequestParam final Map<String, String> query,
            @RequestBody final JsonNode node) throws IOException {
        final Map<String, JsonNode> filter = parser.parse(query);
        final Value value = reader.read(node, Value.class).withKey(id);

        final boolean created = service.create(value);
        final ValuePage page = new ValuePage(service.readAllByKey(id, filter));

        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body(page);
        } else {
            return ResponseEntity.ok(page);
        }
    }

    @RequestMapping(method = DELETE, path = "/keys/{id}/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String id, @RequestParam final Map<String, String> query) throws IOException {
        final Map<String, JsonNode> filter = parser.parse(query);
        service.delete(id, filter);
    }

    @RequestMapping(method = GET, path = "/values")
    public Entries getAll(@RequestParam(name = "q", required = false) @Nullable final String query) throws IOException {
        return new Entries(service.readAllByKeyPattern(query));
    }

}
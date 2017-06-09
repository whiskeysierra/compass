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
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
class ValueResource {

    private final FilterFactory factory;
    private final JsonReader reader;
    private final ValueService service;

    @Autowired
    public ValueResource(final FilterFactory factory, final JsonReader reader,
            final ValueService service) {
        this.factory = factory;
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/keys/{id}/value")
    public Value get(@PathVariable final String id, @RequestParam final Map<String, String> query) throws IOException {
        final Map<String, JsonNode> filter = factory.create(query);
        return service.read(id, filter);
    }

    @RequestMapping(method = GET, path = "/keys/{id}/values")
    public ValuePage getAll(@PathVariable final String id, @RequestParam final Map<String, String> query) throws IOException {
        final Map<String, JsonNode> filter = factory.create(query);
        return new ValuePage(service.readAllByKey(id, filter));
    }

    @RequestMapping(method = POST, path = "/keys/{id}/values")
    public ResponseEntity<ValuePage> post(@PathVariable final String id, @RequestParam final Map<String, String> query,
            @RequestBody final JsonNode node) throws IOException {
        final Map<String, JsonNode> filter = factory.create(query);
        final Value value = reader.read(node, Value.class).withKey(id);

        service.create(value);

        final ValuePage page = new ValuePage(service.readAllByKey(id, filter));

        return ResponseEntity.status(HttpStatus.CREATED).body(page);
    }

    @RequestMapping(method = DELETE, path = "/keys/{id}/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String id, @RequestParam final Map<String, String> query) throws IOException {
        final Map<String, JsonNode> filter = factory.create(query);
        service.delete(id, filter);
    }

    @RequestMapping(method = GET, path = "/values")
    public Entries getAll(@RequestParam(name = "q", required = false) @Nullable final String query) throws IOException {
        return new Entries(service.readAllByKeyPattern(query));
    }

}

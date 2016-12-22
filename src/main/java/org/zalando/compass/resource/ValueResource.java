package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Entries;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ValueResource {

    private final JsonReader reader;
    private final ValueService service;

    @Autowired
    public ValueResource(final JsonReader reader, final ValueService service) {
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/keys/{id}/value")
    public Value get(@PathVariable final String id, @RequestParam final Map<String, String> filter) throws IOException {
        return service.read(id, filter);
    }

    @RequestMapping(method = GET, path = "/keys/{id}/values")
    public Values getAll(@PathVariable final String id, @RequestParam final Map<String, String> filter) throws IOException {
        return new Values(service.readAllByKey(id, filter));
    }

    @RequestMapping(method = POST, path = "/keys/{id}/values")
    public Values post(@PathVariable final String id, @RequestParam final Map<String, String> filter,
            @RequestBody final JsonNode node) throws IOException {
        final Value value = reader.read(node, Value.class).withKey(id);
        service.createOrUpdate(value);
        return new Values(service.readAllByKey(id, filter));
    }

    @RequestMapping(method = DELETE, path = "/keys/{id}/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String id, @RequestParam final Map<String, String> filter) throws IOException {
        service.delete(id, filter);
    }

    @RequestMapping(method = GET, path = "/values")
    public Entries getAll(@RequestParam(name = "q", required = false) @Nullable final String query) throws IOException {
        return new Entries(service.readAllByKeyPattern(query));
    }

}

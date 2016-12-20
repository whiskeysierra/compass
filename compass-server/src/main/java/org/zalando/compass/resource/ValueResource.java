package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;

import java.io.IOException;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

// TODO validate that dimension values can only be primitives
// TODO 404 if key doesn't exist
@RestController
@RequestMapping(path = "/keys/{id}")
public class ValueResource {

    private final ValueService service;

    @Autowired
    public ValueResource(final ValueService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/value")
    public Value get(@PathVariable final String id, @RequestParam final Map<String, String> filter) {
        return service.read(id, filter);
    }

    @RequestMapping(method = GET, path = "/values")
    public Values getAll(@PathVariable final String id, @RequestParam final Map<String, String> filter) {
        return service.readAll(id, filter);
    }

    // TODO document in api.yaml
    // TODO find better name
    @RequestMapping(method = GET, path = "/all-the-values")
    public Values getAll(@PathVariable final String id) {
        return service.readAll(id);
    }

    @RequestMapping(method = POST, path = "/values")
    public Values post(@PathVariable final String id, @RequestBody final Value value) {
        service.createOrUpdate(id, value);
        return service.readAll(id, emptyMap()); // TODO or service.readAll(id)
    }

    @RequestMapping(method = DELETE, path = "/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String id, @RequestParam final Map<String, String> filter) throws IOException {
        service.delete(id, filter);
    }

}

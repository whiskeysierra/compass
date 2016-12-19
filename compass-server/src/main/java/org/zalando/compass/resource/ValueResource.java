package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/keys/{id}")
public class ValueResource {

    private final ValueService service;

    @Autowired
    public ValueResource(final ValueService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/value")
    public Value get(@RequestParam final String id, @RequestParam final Map<String, String> filter) {
        return service.read(id, filter);
    }

    @RequestMapping(method = GET, path = "/values")
    public Values getAll(@RequestParam final String id, @RequestParam final Map<String, String> filter) {
        return service.readAll(id, filter);
    }

    @RequestMapping(method = PUT, path = "/values")
    public Values put(@RequestParam final String id, @RequestBody final Values values) throws IOException {
        service.replace(id, values);
        return values;
    }

    @RequestMapping(method = POST, path = "/values")
    public Values post(@RequestParam final String id, @RequestBody final Values values) {
        service.createOrUpdate(id, values);
        return service.readAll(id, emptyMap());
    }

    @RequestMapping(method = DELETE, path = "/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam final String id, @RequestParam final Map<String, Object> filter) throws IOException {
        service.delete(id, filter);
    }

}

package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Keys;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/keys")
public class KeyResource {

    private final KeyService service;

    @Autowired
    public KeyResource(final KeyService service) {
        this.service = service;
    }

    @RequestMapping(method = GET)
    public Keys getAll() {
        return service.readAll();
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Key get(@RequestParam final String id) {
        return service.read(id);
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Key> put(@RequestParam final String id,
            @RequestBody final Key key) throws IOException {

        checkArgument(id.equals(key.getId()), "ID in path and body must match");

        if (service.createOrUpdate(key)) {
            return ResponseEntity.status(CREATED).body(key);
        } else {
            return ResponseEntity.ok(key);
        }
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@RequestParam final String id) {
        service.delete(id);
    }

}

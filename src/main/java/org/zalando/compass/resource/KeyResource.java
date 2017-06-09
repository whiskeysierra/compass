package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.KeyRepository;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/keys")
class KeyResource {

    private final JsonReader reader;
    private final KeyService service;
    private final KeyRepository repository;

    @Autowired
    public KeyResource(final JsonReader reader, final KeyService service, final KeyRepository repository) {
        this.reader = reader;
        this.service = service;
        this.repository = repository;
    }

    @RequestMapping(method = GET)
    public KeyPage getAll() {
        return new KeyPage(repository.findAll());
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Key get(@PathVariable final String id) throws IOException {
        return repository.read(id);
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Key> put(@PathVariable final String id,
            @RequestBody final JsonNode node) throws IOException {
        final Key input = reader.read(node, Key.class);
        final Key key = ensureConsistentId(id, input);

        if (service.createOrUpdate(key)) {
            return ResponseEntity.status(CREATED).body(key);
        } else {
            return ResponseEntity.ok(key);
        }
    }

    private Key ensureConsistentId(@PathVariable final String id, final Key input) {
        checkArgument(input.getId() == null || id.equals(input.getId()),
                "If present, ID body must match with URL");

        return input.withId(id);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id) {
        service.delete(id);
    }

}

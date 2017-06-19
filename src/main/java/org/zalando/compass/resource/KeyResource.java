package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;

@RestController
@RequestMapping(path = "/keys")
class KeyResource {

    private final JsonReader reader;
    private final KeyService service;

    @Autowired
    public KeyResource(final JsonReader reader, final KeyService service) {
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET)
    public KeyPage getAll(@RequestParam(name = "q", required = false) @Nullable final String q) {
        return new KeyPage(service.readAllByKeyPattern(q));
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Key get(@PathVariable final String id) throws IOException {
        return service.read(id);
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Key> put(@PathVariable final String id,
            @RequestBody final ObjectNode node) throws IOException {
        ensureConsistentId(id, node);
        final Key key = reader.read(node, Key.class);

        final boolean created = service.replace(key);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(key);
    }

    private void ensureConsistentId(@PathVariable final String id, final ObjectNode node) {
        final JsonNode idInBody = node.get("id");
        checkArgument(idInBody == null || id.equals(idInBody.asText()), "If present, ID body must match with URL");

        node.put("id", id);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id) {
        service.delete(id);
    }

}

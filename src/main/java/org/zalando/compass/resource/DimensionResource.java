package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;

@RestController
@RequestMapping(path = "/dimensions")
class DimensionResource {

    private final JsonReader reader;
    private final DimensionService service;

    @Autowired
    public DimensionResource(final JsonReader reader, final DimensionService service) {
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Dimension> replace(@PathVariable final String id,
            @RequestBody final ObjectNode node) throws IOException {

        ensureConsistentId(id, node);
        final Dimension dimension = reader.read(node, Dimension.class);

        final boolean created = service.replace(dimension);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(dimension);
    }

    private void ensureConsistentId(@PathVariable final String id, final ObjectNode node) {
        final JsonNode idInBody = node.get("id");
        checkArgument(idInBody == null || id.equals(idInBody.asText()), "If present, ID body must match with URL");

        node.put("id", id);
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Dimension get(@PathVariable final String id) throws IOException {
        return service.read(id);
    }

    @RequestMapping(method = GET)
    public DimensionPage getAll() {
        return new DimensionPage(service.readAll());
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id) throws IOException {
        service.delete(id);
    }

}

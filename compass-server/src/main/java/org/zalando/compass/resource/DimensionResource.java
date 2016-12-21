package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Dimensions;
import org.zalando.compass.library.JsonReader;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/dimensions")
public class DimensionResource {

    private final JsonReader reader;
    private final DimensionService service;

    @Autowired
    public DimensionResource(final JsonReader reader, final DimensionService service) {
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET)
    public Dimensions getAll() {
        return service.readAll();
    }

    @RequestMapping(method = PUT)
    public Dimensions putAll(@RequestBody final JsonNode node) throws IOException {
        final Dimensions dimensions = reader.read(node, Dimensions.class);
        service.reorder(dimensions.getDimensions());
        return service.readAll();
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Dimension get(@PathVariable final String id) {
        return service.read(id);
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Dimension> put(@PathVariable final String id,
            @RequestBody final JsonNode node) throws IOException {
        final Dimension input = reader.read(node, Dimension.class);

        checkArgument(input.getId() == null || id.equals(input.getId()),
                "If present, ID body must match with URL");

        final Dimension dimension = input.withId(id);

        if (service.createOrUpdate(dimension)) {
            return ResponseEntity.status(CREATED).body(dimension);
        } else {
            return ResponseEntity.ok(dimension);
        }
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id) {
        service.delete(id);
    }

}

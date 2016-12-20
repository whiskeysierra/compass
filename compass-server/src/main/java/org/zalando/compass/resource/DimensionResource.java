package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Dimensions;

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

    private final DimensionService service;

    @Autowired
    public DimensionResource(final DimensionService service) {
        this.service = service;
    }

    @RequestMapping(method = GET)
    public Dimensions getAll() {
        return service.readAll();
    }

    @RequestMapping(method = PUT)
    public Dimensions putAll(@RequestBody final Dimensions dimensions) {
        service.reorder(dimensions.getDimensions());
        return service.readAll();
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Dimension get(@RequestParam final String id) {
        return service.read(id);
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Dimension> put(@RequestParam final String id,
            @RequestBody final Dimension dimension) throws IOException {

        checkArgument(id.equals(dimension.getId()), "ID in path and body must match");

        if (service.createOrUpdate(dimension)) {
            return ResponseEntity.status(CREATED).body(dimension);
        } else {
            return ResponseEntity.ok(dimension);
        }
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@RequestParam final String id) {
        service.delete(id);
    }

}

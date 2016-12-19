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
import org.zalando.compass.domain.persistence.DimensionRepository;

import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
    private final DimensionRepository repository;

    @Autowired
    public DimensionResource(final DimensionService service, final DimensionRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @RequestMapping(method = GET)
    public Dimensions getAll() {
        return new Dimensions(repository.getAll());
    }

    @RequestMapping(method = PUT)
    public Dimensions reorder(@RequestBody final Dimensions dimensions) {
        final List<Dimension> list = dimensions.getDimensions();
        final Map<String, Integer> ranks = new HashMap<>(list.size());

        final ListIterator<Dimension> iterator = list.listIterator();

        while (iterator.hasNext()) {
            ranks.put(iterator.next().getId(), iterator.nextIndex());
        }

        repository.reorder(ranks);

        return dimensions;
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Dimension get(@RequestParam final String id) {
        @Nullable final Dimension dimension = service.getDimension(id);

        if (dimension == null) {
            throw new NotFoundException();
        }

        return dimension;
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Dimension> createOrUpdate(@RequestParam final String id,
            @RequestBody final Dimension dimension) {

        checkArgument(id.equals(dimension.getId()), "ID in path and body must match");

        if (repository.create(dimension)) {
            return ResponseEntity.status(CREATED).body(dimension);
        } else {
            repository.update(dimension);
            return ResponseEntity.ok(dimension);
        }
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@RequestParam final String id) {
        if (!repository.delete(id)) {
            throw new NotFoundException();
        }
    }

}

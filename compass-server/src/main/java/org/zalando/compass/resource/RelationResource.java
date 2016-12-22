package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Relations;
import org.zalando.compass.domain.persistence.RelationRepository;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/relations")
public class RelationResource {

    private final RelationRepository repository;

    @Autowired
    public RelationResource(final RelationRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = GET)
    public Relations getRelations() {
        return new Relations(repository.findAll());
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Relation get(@PathVariable final String id) throws IOException {
        return repository.read(id);
    }

}

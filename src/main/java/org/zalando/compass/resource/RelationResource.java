package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.resource.model.RelationPage;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
class RelationResource {

    private final RelationService service;

    @Autowired
    public RelationResource(final RelationService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/relations")
    public RelationPage getRelations() {
        return new RelationPage(service.readAll());
    }

    @RequestMapping(method = GET, path = "/relations/{id}")
    public Relation get(@PathVariable final String id) {
        return service.read(id);
    }

}

package org.zalando.compass.core.infrastructure.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.core.domain.api.RelationService;
import org.zalando.compass.kernel.domain.model.Relation;

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

package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.model.Relations;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/relations")
public class RelationResource {

    private final RelationService service;

    @Autowired
    public RelationResource(final RelationService service) {
        this.service = service;
    }

    @RequestMapping(method = GET)
    public Relations getRelations() {
        return new Relations(service.readAll());
    }

}

package org.zalando.compass.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.model.Relations;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/relations")
public class RelationResource {

    @RequestMapping(method = GET)
    public Relations getRelations() {
        throw new UnsupportedOperationException();
    }

}

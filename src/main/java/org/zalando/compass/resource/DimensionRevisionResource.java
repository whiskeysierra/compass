package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/dimensions")
class DimensionRevisionResource {

    private final DimensionService service;

    @Autowired
    public DimensionRevisionResource(final DimensionService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public DimensionRevisionPage getRevisions(@PathVariable final String id,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @Nullable @RequestParam(required = false) final Long after) {

        // can actually never happen, just to satisfy IDEA
        checkNotNull(limit, "Limit required");

        final Page<DimensionRevision> page = service.readRevisions(id, limit, after);
        final DimensionRevision next = page.getNext();
        final List<DimensionRevision> revisions = page.getElements();

        final Link link = next == null ?
                null :
                new Link(linkTo(methodOn(DimensionRevisionResource.class)
                        .getRevisions(id, limit, next.getRevision().getId())).toUri());

        return new DimensionRevisionPage(link, revisions);
    }

    @RequestMapping(method = GET, path = "/revisions")
    public Object getRevisions() {
        // TODO implement
        return Collections.emptyMap();
    }

}

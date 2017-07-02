package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/keys")
class KeyRevisionResource {

    private final KeyService service;

    @Autowired
    public KeyRevisionResource(final KeyService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public KeyRevisionPage getRevisions(@PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(required = false) final Long after) {

        final Page<KeyRevision> page = service.readRevisions(id, limit, after);
        final KeyRevision next = page.getNext();
        final List<KeyRevision> revisions = page.getElements();

        final Link link = next == null ?
                null :
                new Link(linkTo(methodOn(KeyRevisionResource.class)
                        .getRevisions(id, limit, next.getRevision().getId())).toUri());

        return new KeyRevisionPage(link, revisions);
    }

    @RequestMapping(method = GET, path = "/{id}/revisions/{revision}")
    public ResponseEntity<KeyRevision> getRevision(@PathVariable final String id, @PathVariable final long revision) {
        return ResponseEntity.ok(service.readRevision(id, revision));
    }

    @RequestMapping(method = GET, path = "/revisions")
    public Object getRevisions() {
        // TODO implement
        return Collections.emptyMap();
    }

    // TODO "/revisions/{revision}

}

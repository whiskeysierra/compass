package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;
import static org.zalando.compass.resource.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.resource.MediaTypes.JSON_PATCH_VALUE;

@RestController
@RequestMapping(path = "/dimensions")
class DimensionResource implements Reserved {

    private final JsonReader reader;
    private final ObjectMapper mapper;
    private final DimensionService service;

    @Autowired
    public DimensionResource(final JsonReader reader, final ObjectMapper mapper, final DimensionService service) {
        this.reader = reader;
        this.mapper = mapper;
        this.service = service;
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Dimension> replace(@PathVariable final String id,
            @RequestBody final JsonNode node) throws IOException {

        ensureConsistentId(id, node);
        final Dimension dimension = reader.read(node, Dimension.class);

        final boolean created = service.replace(dimension);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(dimension);
    }

    private void ensureConsistentId(@PathVariable final String inUrl, final JsonNode node) {
        final JsonNode inBody = node.path("id");

        if (inBody.isMissingNode()) {
            ObjectNode.class.cast(node).put("id", inUrl);
        } else {
            checkArgument(inUrl.equals(inBody.asText()), "If present, ID in body must match with URL");
        }
    }

    // TODO order by relevance? pagination?
    @RequestMapping(method = GET)
    public DimensionPage getAll(@RequestParam(name = "q", required = false) @Nullable final String q) {
        return new DimensionPage(service.readAll(q));
    }

    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Dimension> get(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(service.read(id));
        } catch (final NotFoundException e) {
            final List<DimensionRevision> revisions = service.readRevisions(id, 1, null).getElements();

            if (revisions.isEmpty()) {
                throw e;
            }

            final long revision = getOnlyElement(revisions).getRevision().getId();

            return ResponseEntity
                    .status(GONE)
                    .location(linkTo(methodOn(DimensionRevisionResource.class).getRevision(id, revision)).toUri())
                    .build();
        }
    }


    @RequestMapping(method = PATCH, path = "/{id}", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<Dimension> update(@PathVariable final String id,
            @RequestBody final ObjectNode patch) throws IOException, JsonPatchException {

        final Dimension dimension = service.read(id);
        final ObjectNode node = mapper.valueToTree(dimension);

        final JsonMergePatch mergePatch = JsonMergePatch.fromJson(patch);
        final JsonNode patched = mergePatch.apply(node);
        return replace(id, patched);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<Dimension> update(@PathVariable final String id,
            @RequestBody final ArrayNode patch) throws IOException, JsonPatchException {

        // TODO validate JsonPatch schema?

        final Dimension dimension = service.read(id);
        final JsonNode node = mapper.valueToTree(dimension);

        final JsonPatch jsonPatch = JsonPatch.fromJson(patch);
        final JsonNode patched = jsonPatch.apply(node);
        return replace(id, patched);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id) {
        service.delete(id);
    }

}

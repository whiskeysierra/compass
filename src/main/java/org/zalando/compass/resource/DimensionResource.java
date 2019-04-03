package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.resource.MediaTypes.JSON_PATCH_VALUE;

@RestController
@RequestMapping(path = "/dimensions")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DimensionResource implements Reserved {

    private final ObjectMapper mapper;
    private final DimensionService service;

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<DimensionRepresentation> createOrReplace(@PathVariable final String id,
            @Nullable @RequestHeader(name = IF_NONE_MATCH, required = false) final String ifNoneMatch,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final Dimension body) {

        final Dimension dimension = body.withId(id);

        final boolean created = createOrReplace(dimension, comment, ifNoneMatch);
        final DimensionRepresentation representation = DimensionRepresentation.valueOf(dimension);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(representation);
    }

    private boolean createOrReplace(final Dimension dimension, @Nullable final String comment,
            @Nullable final String ifNoneMatch) {

        if ("*".equals(ifNoneMatch)) {
            service.create(dimension, comment);
            return true;
        } else {
            return service.replace(dimension, comment);
        }
    }

    @RequestMapping(method = GET)
    public ResponseEntity<DimensionCollectionRepresentation> getAll(
            @RequestParam(name = "q", required = false) @Nullable final String q,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @Nullable @RequestParam(value = "_after", required = false) final String after,
            @Nullable @RequestParam(value = "_before", required = false) final String before) {

        final Pagination<String> query = Pagination.create(after, before, requireNonNull(limit));
        final PageResult<Dimension> page = service.readPage(q, query);

        final List<DimensionRepresentation> representations = page.getElements().stream()
                .map(DimensionRepresentation::valueOf)
                .collect(toList());

        return ResponseEntity.ok(new DimensionCollectionRepresentation(
                page.hasNext() ?
                        link(methodOn(DimensionResource.class).getAll(q, limit, page.getTail().getId(), null)) : null,
                page.hasPrevious() ?
                        link(methodOn(DimensionResource.class).getAll(q, limit, null, page.getHead().getId())) : null,
                representations));
    }

    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<DimensionRepresentation> get(@PathVariable final String id) {
        final Revisioned<Dimension> revisioned = service.read(id);
        return Conditional.build(revisioned, DimensionRepresentation::valueOf);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<DimensionRepresentation> update(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonMergePatch patch) throws IOException, JsonPatchException {

        final Dimension before = service.readOnly(id);
        final JsonNode node = mapper.valueToTree(before);

        final JsonNode patched = patch.apply(node);
        final Dimension after = mapper.treeToValue(patched, Dimension.class);

        return createOrReplace(id, null, comment, after);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<DimensionRepresentation> update(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonPatch patch) throws IOException, JsonPatchException {

        final Dimension before = service.readOnly(id);
        final JsonNode node = mapper.valueToTree(before);

        final JsonNode patched = patch.apply(node);
        final Dimension after = mapper.treeToValue(patched, Dimension.class);

        return createOrReplace(id, null, comment, after);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment) {
        service.delete(id, comment);
    }

}

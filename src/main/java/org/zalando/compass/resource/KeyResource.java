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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.resource.MediaTypes.JSON_PATCH_VALUE;

@RestController
@RequestMapping(path = "/keys")
class KeyResource implements Reserved {

    private final JsonReader reader;
    private final ObjectMapper mapper;
    private final KeyService service;

    @Autowired
    public KeyResource(final JsonReader reader, final ObjectMapper mapper, final KeyService service) {
        this.reader = reader;
        this.mapper = mapper;
        this.service = service;
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<KeyRepresentation> replace(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonNode node) throws IOException {

        ensureConsistentId(id, node);
        final Key key = reader.read(node, Key.class);

        final boolean created = service.replace(key, comment);
        final KeyRepresentation representation = KeyRepresentation.valueOf(key);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(representation);
    }

    private void ensureConsistentId(@PathVariable final String inUrl, final JsonNode node) {
        final JsonNode inBody = node.path("id");

        if (inBody.isMissingNode()) {
            ObjectNode.class.cast(node).put("id", inUrl);
        } else {
            checkArgument(inUrl.equals(inBody.asText()), "If present, ID in body must match with URL");
        }
    }

    @RequestMapping(method = GET)
    public ResponseEntity<KeyCollectionRepresentation> getAll(
            @RequestParam(name = "q", required = false) @Nullable final String q,
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(value = "_after", required = false) final String after,
            @Nullable @RequestParam(value = "_before", required = false) final String before) {

        final Pagination<String> query = Pagination.create(after, before, limit);
        final PageResult<Key> page = service.readPage(q, query);

        final List<KeyRepresentation> representations = page.getElements().stream()
                .map(KeyRepresentation::valueOf)
                .collect(toList());

        return ResponseEntity.ok(new KeyCollectionRepresentation(
                page.hasNext() ?
                        link(methodOn(KeyResource.class).getAll(q, limit, page.getTail().getId(), null)) : null,
                page.hasPrevious() ?
                        link(methodOn(KeyResource.class).getAll(q, limit, null, page.getHead().getId())) : null,
                representations));
    }

    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<KeyRepresentation> get(@PathVariable final String id) {
        return ResponseEntity.ok(KeyRepresentation.valueOf(service.read(id)));
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<KeyRepresentation> update(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ObjectNode patch) throws IOException, JsonPatchException {

        final Key key = service.read(id);
        final ObjectNode node = mapper.valueToTree(key);

        final JsonMergePatch mergePatch = JsonMergePatch.fromJson(patch);
        final JsonNode patched = mergePatch.apply(node);
        return replace(id, comment, patched);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<KeyRepresentation> update(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ArrayNode content) throws IOException, JsonPatchException {

        final JsonPatch patch = reader.read(content, JsonPatch.class);

        final Key key = service.read(id);
        final JsonNode node = mapper.valueToTree(key);

        final JsonNode patched = patch.apply(node);
        return replace(id, comment, patched);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment) {
        service.delete(id, comment);
    }

}

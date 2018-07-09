package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Streams.mapWithIndex;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;
import static org.springframework.http.HttpStatus.CREATED;
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
@RequestMapping(path = "/keys/{key}")
class ValueResource {

    private final Querying querying;
    private final JsonReader reader;
    private final ObjectMapper mapper;
    private final ValueService service;

    @Autowired
    public ValueResource(final Querying querying, final JsonReader reader, final ObjectMapper mapper,
            final ValueService service) {
        this.querying = querying;
        this.reader = reader;
        this.mapper = mapper;
        this.service = service;
    }

    @RequestMapping(method = PUT, path = "/values")
    public ResponseEntity<ValueCollectionRepresentation> replaceAll(@PathVariable final String key,
            @Nullable @RequestHeader(name = IF_NONE_MATCH, required = false) final String ifNoneMatch,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonNode node) throws IOException {

        final ValueCollectionRepresentation representation = reader.read(node, ValueCollectionRepresentation.class);
        final List<ValueRepresentation> representations = representation.getValues();

        final Stream<ValueRepresentation> withDimensions = representations.stream()
                .map(value -> value.withDimensions(firstNonNull(value.getDimensions(), ImmutableMap.of())));
        final List<Value> values = mapWithIndex(withDimensions, ValueRepresentation::toValue).collect(toList());

        final boolean created = createOrReplace(key, values, comment, ifNoneMatch);

        return ResponseEntity.status(created ? CREATED : OK)
                .body(readAll(key, emptyMap()));
    }

    private boolean createOrReplace(final String key, final List<Value> values, @Nullable final String comment,
            @Nullable final String ifNoneMatch) {

        if ("*".equals(ifNoneMatch)) {
            service.create(key, values, comment);
            return true;
        } else {
            return service.replace(key, values, comment);
        }
    }

    @RequestMapping(method = PUT, path = "/value")
    public ResponseEntity<ValueRepresentation> createOrReplace(@PathVariable final String key,
            @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = IF_NONE_MATCH, required = false) final String ifNoneMatch,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonNode node) throws IOException {

        final ImmutableMap<String, JsonNode> dimensions = querying.read(query);
        final Value value = reader.read(node, Value.class).withDimensions(dimensions);

        final boolean created = createOrReplace(key, value, comment, ifNoneMatch);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .location(canonicalUrl(key, value))
                .body(ValueRepresentation.valueOf(value));
    }

    private boolean createOrReplace(final String key, final Value value, @Nullable final String comment,
            @Nullable final String ifNoneMatch) {
        if ("*".equals(ifNoneMatch)) {
            service.create(key, value, comment);
            return true;
        } else {
            return service.replace(key, value, comment);
        }
    }

    @RequestMapping(method = GET, path = "/values")
    public ValueCollectionRepresentation readAll(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final List<Value> page = service.readPage(key, filter);
        final List<ValueRepresentation> representations = page.stream()
                .map(ValueRepresentation::valueOf).collect(toList());

        return new ValueCollectionRepresentation(representations);
    }

    @RequestMapping(method = GET, path = "/value")
    public ResponseEntity<ValueRepresentation> read(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final Value value = service.read(key, filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, canonicalUrl(key, value).toASCIIString())
                .body(ValueRepresentation.valueOf(value));
    }

    @RequestMapping(method = PATCH, path = "/values", consumes = {APPLICATION_JSON_VALUE, JSON_PATCH_VALUE})
    public ResponseEntity<ValueCollectionRepresentation> updateAll(@PathVariable final String key,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ArrayNode content) throws IOException, JsonPatchException {

        final JsonPatch patch = reader.read(content, JsonPatch.class);

        final ValueCollectionRepresentation values = readAll(key, emptyMap());
        final JsonNode node = mapper.valueToTree(values);

        final JsonNode patched = patch.apply(node);
        return replaceAll(key, null, comment, patched);
    }

    @RequestMapping(method = PATCH, path = "/value", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<ValueRepresentation> update(@PathVariable final String key,
            @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ObjectNode content) throws IOException, JsonPatchException {

        final Map<String, JsonNode> filter = querying.read(query);
        final Value value = service.read(key, filter);
        final JsonNode node = mapper.valueToTree(value);

        final JsonMergePatch patch = JsonMergePatch.fromJson(content);
        final JsonNode patched = patch.apply(node);
        return createOrReplace(key, query, null, comment, patched);
    }

    @RequestMapping(method = PATCH, path = "/value", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<ValueRepresentation> update(@PathVariable final String key,
            @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ArrayNode content) throws IOException, JsonPatchException {

        final JsonPatch patch = reader.read(content, JsonPatch.class);

        final Map<String, JsonNode> filter = querying.read(query);
        final Value value = service.read(key, filter);
        final JsonNode node = mapper.valueToTree(value);

        final JsonNode patched = patch.apply(node);
        return createOrReplace(key, query, null, comment, patched);
    }

    @RequestMapping(method = DELETE, path = "/value")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String key, @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment) {
        final Map<String, JsonNode> filter = querying.read(query);
        service.delete(key, filter, comment);
    }

    private URI canonicalUrl(final String key, final Value value) {
        final Map<String, String> query = querying.write(value.getDimensions());
        return link(methodOn(ValueResource.class).read(key, query));
    }

}

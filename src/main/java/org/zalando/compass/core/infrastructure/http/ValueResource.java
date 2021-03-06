package org.zalando.compass.core.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
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
import org.zalando.compass.core.domain.api.BadArgumentException;
import org.zalando.compass.core.domain.api.DimensionService;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.api.ValueService;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.library.Querying;
import org.zalando.fauxpas.ThrowingUnaryOperator;

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
import static org.zalando.compass.core.infrastructure.http.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.core.infrastructure.http.MediaTypes.JSON_PATCH_VALUE;
import static org.zalando.compass.library.Linking.link;
import static org.zalando.compass.library.Maps.transform;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ValueResource {

    private final Querying querying;
    private final ObjectMapper mapper;
    private final ValueService service;
    private final DimensionService dimensionService;

    @SuppressWarnings("UnstableApiUsage")
    @RequestMapping(method = PUT, path = "/keys/{key}/values")
    public ResponseEntity<ValueCollectionRepresentation> replaceAll(
            @PathVariable final String key,
            @Nullable @RequestHeader(name = IF_NONE_MATCH, required = false) final String ifNoneMatch,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ValueCollectionRepresentation representation) {

        final var representations = representation.getValues();

        final var withDimensions = representations.stream()
                .map(value -> value.withDimensions(firstNonNull(value.getDimensions(), ImmutableMap.of())));
        final var values = mapWithIndex(withDimensions, this::toValue).collect(toList());

        final var created = createOrReplace(key, values, comment, ifNoneMatch);

        // TODO share logic via private method
        return ResponseEntity.status(created ? CREATED : OK)
                .body(readAll(key, emptyMap()).getBody());
    }

    private Value toValue(final ValueRepresentation value, final long index) {
        return new Value(transform(value.getDimensions(), dimensionService::readOnly), index, value.getValue());
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

    @RequestMapping(method = PUT, path = "/keys/{key}/value")
    public ResponseEntity<ValueRepresentation> createOrReplace(
            @PathVariable final String key,
            @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = IF_NONE_MATCH, required = false) final String ifNoneMatch,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final ValueRepresentation body) {

        final ImmutableMap<Dimension, JsonNode> dimensions;

        try {
            dimensions = transform(querying.read(query),
                    dimensionService::readOnly);
        } catch (final NotFoundException e) {
            throw new BadArgumentException(e);
        }

        final var value = new Value(dimensions, null, body.getValue());

        final var created = createOrReplace(key, value, comment, ifNoneMatch);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .location(canonicalUrl(key, value))
                // TODO etag?!
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

    @RequestMapping(method = GET, path = "/keys/{key}/values")
    public ResponseEntity<ValueCollectionRepresentation> readAll(@PathVariable final String key,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final var revisioned = service.readPage(key, transform(filter, dimensionService::readOnly));

        return Conditional.build(revisioned, page ->
                new ValueCollectionRepresentation(page.stream()
                        .map(ValueRepresentation::valueOf).collect(toList())));
    }

    @RequestMapping(method = GET, path = "/keys/{key}/value")
    public ResponseEntity<ValueRepresentation> read(@PathVariable final String key,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final var revisioned = service.read(key, transform(filter, dimensionService::readOnly));
        final var value = revisioned.getEntity();

        return Conditional.builder(revisioned)
                .header(HttpHeaders.CONTENT_LOCATION, canonicalUrl(key, value).toASCIIString())
                .body(ValueRepresentation.valueOf(value));
    }

    @RequestMapping(method = PATCH, path = "/keys/{key}/values", consumes = {APPLICATION_JSON_VALUE, JSON_PATCH_VALUE})
    public ResponseEntity<ValueCollectionRepresentation> updateAll(@PathVariable final String key,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonPatch patch) throws IOException, JsonPatchException {

        // TODO share logic via private method
        final var before = readAll(key, emptyMap()).getBody();
        final var node = mapper.valueToTree(before);

        final var patched = patch.apply(node);
        final var after = mapper.treeToValue(patched, ValueCollectionRepresentation.class);

        return replaceAll(key, null, comment, after);
    }

    @RequestMapping(method = PATCH, path = "/keys/{key}/value", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<ValueRepresentation> update(@PathVariable final String key,
            @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonMergePatch patch) throws IOException, JsonPatchException {

        return patch(key, query, comment, patch::apply);
    }

    @RequestMapping(method = PATCH, path = "/keys/{key}/value", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<ValueRepresentation> update(@PathVariable final String key,
            @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonPatch patch) throws IOException, JsonPatchException {

        return patch(key, query, comment, patch::apply);
    }

    private ResponseEntity<ValueRepresentation> patch(final String key, final Map<String, String> query,
            final @Nullable String comment,
            final ThrowingUnaryOperator<JsonNode, JsonPatchException> patch) throws IOException, JsonPatchException {
        final Map<String, JsonNode> filter = querying.read(query);

        final var before = ValueRepresentation.valueOf(
                service.readOnly(key, transform(filter, dimensionService::readOnly)));
        final var node = mapper.valueToTree(before);

        final var patched = patch.tryApply(node);
        final var after = mapper.treeToValue(patched, ValueRepresentation.class);

        return createOrReplace(key, query, null, comment, after);
    }

    @RequestMapping(method = DELETE, path = "/keys/{key}/value")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String key, @RequestParam final Map<String, String> query,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment) {
        final Map<String, JsonNode> filter = querying.read(query);
        service.delete(key, transform(filter, dimensionService::readOnly), comment);
    }

    private URI canonicalUrl(final String key, final Value value) {
        final Map<String, String> query = querying.write(value.getDimensions());
        return link(methodOn(ValueResource.class).read(key, query));
    }

}

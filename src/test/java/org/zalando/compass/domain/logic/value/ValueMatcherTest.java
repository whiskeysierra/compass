package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.library.Schema;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueMatcherTest {

    private final DimensionService dimensionService = mock(DimensionService.class);
    private final RelationService relationService = mock(RelationService.class);
    
    private final List<Value> values = ImmutableList.of(
            new Value(of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
            new Value(of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
            new Value(of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
            new Value(of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
            new Value(of("country", text("DE"), "after", text("2018-01-01T00:00:00Z")), decimal(0.22)),
            new Value(of("country", text("DE"), "after", text("2017-01-01T00:00:00Z")), decimal(0.2)),
            new Value(of("country", text("DE"), "postal-code", text("27498")), decimal(0.0)),
            new Value(of("country", text("CH")), decimal(0.07)),
            new Value(of("country", text("DE")), decimal(0.19)),
            new Value(of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
            new Value(of("locale", text("de-DE")), decimal(0.19)),
            new Value(of("locale", text("en-DE")), decimal(0.18)),
            new Value(of("locale", text("de")), decimal(0.10)),
            new Value(of("email", text(".*@zalando\\.de")), decimal(0.0)),
            new Value(of("email", text(".*@goldmansachs\\.com")), decimal(1.0)),
            new Value(of(), decimal(0.25))
    );

    private final ValueMatcher unit = new ValueMatcher(dimensionService, relationService);

    @Before
    public void defaultBehaviour() {
        when(dimensionService.readAll(any())).thenReturn(asList(
                new Dimension("after", Schema.stringSchema(), ">=", ""),
                new Dimension("before", Schema.stringSchema(), "<=", ""),
                new Dimension("country", Schema.stringSchema(), "=", ""),
                new Dimension("postal-code", Schema.stringSchema(), "=", ""),
                new Dimension("locale", Schema.stringSchema(), "^", ""),
                new Dimension("email", Schema.stringSchema(), "~", "")));

        final Map<String, Relation> relations = stream(load(Relation.class).spliterator(), false)
                .collect(toMap(Relation::getId, identity()));

        when(relationService.read(any())).thenAnswer(invocation ->
                relations.get(invocation.<String>getArgument(0)));
    }

    @Test
    public void shouldMatchEquality() throws IOException {
        assertThat(unit.match(values, of("country", text("DE"))), contains(
                new Value(of("country", text("DE")), decimal(0.19)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchEqualityFallback() throws IOException {
        assertThat(unit.match(values, of("country", text("UK"))), contains(
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchLessThan() throws IOException {
        assertThat(unit.match(values, of("country", text("CH"), "before", text("2013-12-20T11:47:19Z"))), contains(
                new Value(of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new Value(of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new Value(of("country", text("CH")), decimal(0.07)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchLessThanEqual() throws IOException {
        assertThat(unit.match(values, of("country", text("CH"), "before", text("2014-01-01T00:00:00Z"))), contains(
                new Value(of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new Value(of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new Value(of("country", text("CH")), decimal(0.07)),
                new Value(of(), decimal(0.25))
        ));
    }

    @Test
    public void shouldMatchGreaterThan() throws IOException {
        assertThat(unit.match(values, of("country", text("CH"), "after", text("2019-12-20T11:47:19Z"))), contains(
                new Value(of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new Value(of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new Value(of("country", text("CH")), decimal(0.07)),
                new Value(of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchGreaterThanEqual() throws IOException {
        assertThat(unit.match(values, of("country", text("CH"), "after", text("2018-01-01T00:00:00Z"))), contains(
                new Value(of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new Value(of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new Value(of("country", text("CH")), decimal(0.07)),
                new Value(of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchPrefix() throws IOException {
        assertThat(unit.match(values, of("locale", text("de-AT"))),contains(
                new Value(of("locale", text("de")), decimal(0.10)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchMatches() throws IOException {
        assertThat(unit.match(values, of("email", text("user@zalando.de"))), contains(
                new Value(of("email", text(".*@zalando\\.de")), decimal(0.0)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithoutFilter() throws IOException {
        assertThat(unit.match(values, of()), contains(
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithUnknownDimensions() throws IOException {
        assertThat(unit.match(values, of("foo", text("bar"))), contains(
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithoutMatchingDimensions() throws IOException {
        assertThat(unit.match(values, of("postal-code", text("12345"))), contains(
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithPartiallyUnknownDimensions() throws IOException {
        assertThat(unit.match(values, of("country", text("DE"), "foo", text("bar"))), contains(
                new Value(of("country", text("DE")), decimal(0.19)),
                new Value(of(), decimal(0.25))));
    }

    private JsonNode text(final String text) {
        return new TextNode(text);
    }

    private JsonNode decimal(final double v) {
        return new DecimalNode(new BigDecimal(v));
    }

}
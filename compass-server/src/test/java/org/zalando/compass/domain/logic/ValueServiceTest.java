package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.math.BigDecimal;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueServiceTest {

    private final SchemaValidator validator = mock(SchemaValidator.class);
    private final RelationService relationService = new RelationService();
    private final DimensionRepository dimensionRepository = mock(DimensionRepository.class);
    private final KeyService keyService = mock(KeyService.class);
    private final ValueRepository valueRepository = mock(ValueRepository.class);

    private final ValueService unit = new ValueService(validator, relationService, dimensionRepository, keyService,
            valueRepository
    );

    @Before
    public void setUp() throws Exception {
        when(dimensionRepository.readAll()).thenReturn(asList(
                new Dimension("after", stringSchema(), ">=", ""),
                new Dimension("before", stringSchema(), "<=", ""),
                new Dimension("country", stringSchema(), "=", ""),
                new Dimension("postal-code", stringSchema(), "=", ""),
                new Dimension("locale", stringSchema(), "^", ""),
                new Dimension("email", stringSchema(), "~", "")));

        when(keyService.exists("tax-rate")).thenReturn(true);

        final List<Value> values = asList(
                new Value(of(), decimal(0.25)), // legally questionable, but ok for the sake of a test
                new Value(of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new Value(of("country", text("CH")), decimal(0.07)),
                new Value(of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new Value(of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new Value(of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new Value(of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new Value(of("country", text("DE")), decimal(0.19)),
                new Value(of("country", text("DE"), "after", text("2017-01-01T00:00:00Z")), decimal(0.2)),
                new Value(of("country", text("DE"), "after", text("2018-01-01T00:00:00Z")), decimal(0.22)),
                new Value(of("country", text("DE"), "postal-code", text("27498")), decimal(0.0)),
                new Value(of("locale", text("de")), decimal(0.10)),
                new Value(of("locale", text("de-DE")), decimal(0.19)),
                new Value(of("locale", text("en-DE")), decimal(0.18)),
                new Value(of("email", text(".*@zalando\\.de")), decimal(0.0)),
                new Value(of("email", text(".*@goldmansachs\\.com")), decimal(1.0)));

        when(valueRepository.readAllByKey(any())).thenReturn(values);
        when(valueRepository.readAllByKeyPattern(any())).thenReturn(values);
    }

    private ObjectNode stringSchema() {
        return new ObjectNode(instance).put("type", "string");
    }

    @Test
    public void shouldReadEquality() {
        assertThat(unit.read("tax-rate", of("country", "DE")).getValue(), is(decimal(0.19)));
    }

    @Test
    public void shouldReadEqualityFallback() {
        assertThat(unit.read("tax-rate", of("country", "UK")).getValue(), is(decimal(0.25)));
    }

    @Test
    public void shouldReadLessThan() {
        assertThat(unit.read("tax-rate",
                of("country", "CH", "before", "2013-12-20T11:47:19Z")).getValue(), is(decimal(0.05)));
    }

    @Test
    public void shouldReadLessThanEqual() {
        assertThat(unit.read("tax-rate",
                of("country", "CH", "before", "2014-01-01T00:00:00Z")).getValue(), is(decimal(0.05)));
    }

    @Test
    public void shouldReadGreaterThan() {
        assertThat(unit.read("tax-rate",
                of("country", "CH", "after", "2019-12-20T11:47:19Z")).getValue(), is(decimal(0.09)));
    }

    @Test
    public void shouldReadGreaterThanEqual() {
        assertThat(unit.read("tax-rate",
                of("country", "CH", "after", "2018-01-01T00:00:00Z")).getValue(), is(decimal(0.09)));
    }

    @Test
    public void shouldReadPrefix() {
        assertThat(unit.read("tax-rate", of("locale", "de-AT")).getValue(), is(decimal(0.10)));
    }

    @Test
    public void shouldReadMatches() {
        assertThat(unit.read("tax-rate", of("email", "user@zalando.de")).getValue(), is(decimal(0.0)));
    }

    @Test
    public void shouldReadFallback() {
        assertThat(unit.read("tax-rate", of()).getValue(), is(decimal(0.25)));
    }

    @Test
    public void shouldReadAllEqualityWithFilter() {
        assertThat(unit.readAllByKey("tax-rate", of("country", "DE")).getValues(), contains(
                new Value(of("country", text("DE")), decimal(0.19)),
                new Value(of(), decimal(0.25))));
    }

    @Test
    public void shouldFindAll() {
        assertThat(unit.readAllByKeyPattern("tax").getValues(), contains(
                new Value(of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new Value(of("country", text("DE"), "after", text("2018-01-01T00:00:00Z")), decimal(0.22)),
                new Value(of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new Value(of("country", text("DE"), "after", text("2017-01-01T00:00:00Z")), decimal(0.2)),
                new Value(of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new Value(of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new Value(of("country", text("DE"), "postal-code", text("27498")), decimal(0.0)),
                new Value(of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new Value(of("country", text("CH")), decimal(0.07)),
                new Value(of("country", text("DE")), decimal(0.19)),
                new Value(of("locale", text("de-DE")), decimal(0.19)),
                new Value(of("locale", text("en-DE")), decimal(0.18)),
                new Value(of("locale", text("de")), decimal(0.10)),
                new Value(of("email", text(".*@goldmansachs\\.com")), decimal(1.0)),
                new Value(of("email", text(".*@zalando\\.de")), decimal(0.0)),
                new Value(of(), decimal(0.25))));
    }

    private JsonNode text(final String v) {
        return new TextNode(v);
    }

    private JsonNode decimal(final double v) {
        return new DecimalNode(new BigDecimal(v));
    }

}
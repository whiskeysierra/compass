package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueRepository;
import org.zalando.compass.library.Schema;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKeyPattern;

public class ValueServiceTest {

    private final SchemaValidator validator = mock(SchemaValidator.class);
    private final RelationRepository relationRepository = new RelationRepository();
    private final DimensionRepository dimensionRepository = mock(DimensionRepository.class);
    private final KeyRepository keyRepository = mock(KeyRepository.class);
    private final ValueRepository valueRepository = mock(ValueRepository.class);

    private final ValueService unit = new ValueService(validator, relationRepository, dimensionRepository,
            keyRepository, valueRepository);

    @Before
    public void setUp() throws Exception {
        when(dimensionRepository.findAll()).thenReturn(asList(
                new Dimension("after", Schema.stringSchema(), ">=", ""),
                new Dimension("before", Schema.stringSchema(), "<=", ""),
                new Dimension("country", Schema.stringSchema(), "=", ""),
                new Dimension("postal-code", Schema.stringSchema(), "=", ""),
                new Dimension("locale", Schema.stringSchema(), "^", ""),
                new Dimension("email", Schema.stringSchema(), "~", "")));

        when(keyRepository.exists("tax-rate")).thenReturn(true);

        final List<Value> values = asList(
                new Value("tax-rate", of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new Value("tax-rate", of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new Value("tax-rate", of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new Value("tax-rate", of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new Value("tax-rate", of("country", text("DE"), "after", text("2018-01-01T00:00:00Z")), decimal(0.22)),
                new Value("tax-rate", of("country", text("DE"), "after", text("2017-01-01T00:00:00Z")), decimal(0.2)),
                new Value("tax-rate", of("country", text("DE"), "postal-code", text("27498")), decimal(0.0)),
                new Value("tax-rate", of("country", text("CH")), decimal(0.07)),
                new Value("tax-rate", of("country", text("DE")), decimal(0.19)),
                new Value("tax-rate", of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new Value("tax-rate", of("locale", text("de-DE")), decimal(0.19)),
                new Value("tax-rate", of("locale", text("en-DE")), decimal(0.18)),
                new Value("tax-rate", of("locale", text("de")), decimal(0.10)),
                new Value("tax-rate", of("email", text(".*@zalando\\.de")), decimal(0.0)),
                new Value("tax-rate", of("email", text(".*@goldmansachs\\.com")), decimal(1.0)),
                new Value("tax-rate", of(), decimal(0.25)) // legally questionable, but ok for the sake of a test
        );

        when(valueRepository.findAll(byKey(any()))).thenReturn(values);
        when(valueRepository.findAll(byKeyPattern(any()))).thenReturn(values);
    }

    @Test
    public void shouldReadEquality() throws IOException {
        assertThat(unit.read("tax-rate", of("country", text("DE"))).getValue(), is(decimal(0.19)));
    }

    @Test
    public void shouldReadEqualityFallback() throws IOException {
        assertThat(unit.read("tax-rate", of("country", text("UK"))).getValue(), is(decimal(0.25)));
    }

    @Test
    public void shouldReadLessThan() throws IOException {
        assertThat(unit.read("tax-rate",
                of("country", text("CH"), "before", text("2013-12-20T11:47:19Z"))).getValue(), is(decimal(0.05)));
    }

    @Test
    public void shouldReadLessThanEqual() throws IOException {
        assertThat(unit.read("tax-rate",
                of("country", text("CH"), "before", text("2014-01-01T00:00:00Z"))).getValue(), is(decimal(0.05)));
    }

    @Test
    public void shouldReadGreaterThan() throws IOException {
        assertThat(unit.read("tax-rate",
                of("country", text("CH"), "after", text("2019-12-20T11:47:19Z"))).getValue(), is(decimal(0.09)));
    }

    @Test
    public void shouldReadGreaterThanEqual() throws IOException {
        assertThat(unit.read("tax-rate",
                of("country", text("CH"), "after", text("2018-01-01T00:00:00Z"))).getValue(), is(decimal(0.09)));
    }

    @Test
    public void shouldReadPrefix() throws IOException {
        assertThat(unit.read("tax-rate", of("locale", text("de-AT"))).getValue(), is(decimal(0.10)));
    }

    @Test
    public void shouldReadMatches() throws IOException {
        assertThat(unit.read("tax-rate", of("email", text("user@zalando.de"))).getValue(), is(decimal(0.0)));
    }

    @Test
    public void shouldReadFallback() throws IOException {
        assertThat(unit.read("tax-rate", of()).getValue(), is(decimal(0.25)));
    }

    @Test
    public void shouldReadAllEqualityWithFilter() throws IOException {
        assertThat(unit.readAllByKey("tax-rate", of("country", text("DE"))), contains(
                new Value("tax-rate", of("country", text("DE")), decimal(0.19)),
                new Value("tax-rate", of(), decimal(0.25))));
    }

    @Test
    public void shouldFindAll() throws IOException {
        assertThat(unit.readAllByKeyPattern("tax").get("tax-rate"), contains(
                new Value("tax-rate", of("country", text("CH"), "before", text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new Value("tax-rate", of("country", text("CH"), "before", text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new Value("tax-rate", of("country", text("CH"), "after", text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new Value("tax-rate", of("country", text("CH"), "after", text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new Value("tax-rate", of("country", text("DE"), "after", text("2018-01-01T00:00:00Z")), decimal(0.22)),
                new Value("tax-rate", of("country", text("DE"), "after", text("2017-01-01T00:00:00Z")), decimal(0.2)),
                new Value("tax-rate", of("country", text("DE"), "postal-code", text("27498")), decimal(0.0)),
                new Value("tax-rate", of("country", text("CH")), decimal(0.07)),
                new Value("tax-rate", of("country", text("DE")), decimal(0.19)),
                new Value("tax-rate", of("after", text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new Value("tax-rate", of("locale", text("de-DE")), decimal(0.19)),
                new Value("tax-rate", of("locale", text("en-DE")), decimal(0.18)),
                new Value("tax-rate", of("locale", text("de")), decimal(0.10)),
                new Value("tax-rate", of("email", text(".*@zalando\\.de")), decimal(0.0)),
                new Value("tax-rate", of("email", text(".*@goldmansachs\\.com")), decimal(1.0)),
                new Value("tax-rate", of(), decimal(0.25))));
    }

    private JsonNode text(final String text) {
        return new TextNode(text);
    }

    private JsonNode decimal(final double v) {
        return new DecimalNode(new BigDecimal(v));
    }

}
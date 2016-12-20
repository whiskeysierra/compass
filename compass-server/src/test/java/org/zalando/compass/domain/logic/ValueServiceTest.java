package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.zalando.compass.domain.logic.relations.Equality;
import org.zalando.compass.domain.logic.relations.GreaterThan;
import org.zalando.compass.domain.logic.relations.LessThan;
import org.zalando.compass.domain.logic.relations.Matches;
import org.zalando.compass.domain.logic.relations.Prefix;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

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

    private final ValueRepository valueRepository = mock(ValueRepository.class);
    private final DimensionRepository dimensionRepository = mock(DimensionRepository.class);
    private final RelationService relationService = mock(RelationService.class);

    private final ValueService unit = new ValueService(valueRepository, dimensionRepository, relationService);

    @Before
    public void setUp() throws Exception {
        when(valueRepository.readAll(any())).thenReturn(asList(
                new Value(of(), 0.25), // legally questionable, but ok for the sake of a test
                new Value(of("after", "2017-01-01T00:00:00Z"), 0.5),
                new Value(of("country", "CH"), 0.07),
                new Value(of("country", "CH", "after", "2017-01-01T00:00:00Z"), 0.08),
                new Value(of("country", "CH", "after", "2018-01-01T00:00:00Z"), 0.09),
                new Value(of("country", "CH", "before", "2015-01-01T00:00:00Z"), 0.06),
                new Value(of("country", "CH", "before", "2014-01-01T00:00:00Z"), 0.05),
                new Value(of("country", "DE"), 0.19),
                new Value(of("country", "DE", "after", "2017-01-01T00:00:00Z"), 0.2),
                new Value(of("country", "DE", "after", "2018-01-01T00:00:00Z"), 0.22),
                new Value(of("country", "DE", "postal-code", "27498"), 0.0),
                new Value(of("locale", "de"), 0.10),
                new Value(of("locale", "de-DE"), 0.19),
                new Value(of("locale", "en-DE"), 0.18),
                new Value(of("email", ".*@zalando\\.com"), 0.0),
                new Value(of("email", ".*@goldmansachs\\.com"), 1.0)));

        when(dimensionRepository.readAll()).thenReturn(asList(
                new Dimension("after", stringSchema(), ">", ""),
                new Dimension("before", stringSchema(), "<", ""),
                new Dimension("country", stringSchema(), "=", ""),
                new Dimension("postal-code", stringSchema(), "=", ""),
                new Dimension("locale", stringSchema(), "prefix", ""),
                new Dimension("email", stringSchema(), "~", "")));

        when(relationService.readAll())
                .thenReturn(asList(new Equality(), new LessThan(), new GreaterThan(), new Matches(), new Prefix()));
    }

    private ObjectNode stringSchema() {
        return new ObjectNode(instance).put("type", "string");
    }

    @Test
    public void shouldRead() {
        assertThat(unit.read("tax-rate", of("country", "DE")).getValue(), is(0.19));
    }

    @Test
    public void shouldReadAll() {
        assertThat(unit.readAll("tax-rate", of()).getValues(), contains(
                new Value(of("country", "CH", "after", "2018-01-01T00:00:00Z"), 0.09),
                new Value(of("country", "DE", "after", "2018-01-01T00:00:00Z"), 0.22),
                new Value(of("country", "CH", "after", "2017-01-01T00:00:00Z"), 0.08),
                new Value(of("country", "DE", "after", "2017-01-01T00:00:00Z"), 0.2),
                new Value(of("country", "CH", "before", "2014-01-01T00:00:00Z"), 0.05),
                new Value(of("country", "CH", "before", "2015-01-01T00:00:00Z"), 0.06),
                new Value(of("country", "DE", "postal-code", "27498"), 0.0),
                new Value(of("after", "2017-01-01T00:00:00Z"), 0.5),
                new Value(of("country", "CH"), 0.07),
                new Value(of("country", "DE"), 0.19),
                new Value(of("locale", "de-DE"), 0.19),
                new Value(of("locale", "en-DE"), 0.18),
                new Value(of("locale", "de"), 0.10),
                new Value(of("email", ".*@goldmansachs\\.com"), 1.0),
                new Value(of("email", ".*@zalando\\.com"), 0.0),
                new Value(of(), 0.25)));
    }

}
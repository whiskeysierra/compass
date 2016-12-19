package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.Arrays;

import static java.util.Collections.singletonList;
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
        when(valueRepository.readAll(any())).thenReturn(Arrays.asList(
                new Value(ImmutableMap.of(), 0.25), // questionable
                new Value(ImmutableMap.of("country", "CH"), 0.07),
                new Value(ImmutableMap.of("country", "CH", "after", "2017-01-01T00:00:00Z"), 0.08),
                new Value(ImmutableMap.of("country", "CH", "before", "2015-01-01T00:00:00Z"), 0.06),
                new Value(ImmutableMap.of("country", "DE"), 0.19),
                new Value(ImmutableMap.of("country", "DE", "after", "2017-01-01T00:00:00Z"), 0.2),
                new Value(ImmutableMap.of("country", "DE", "postal-code", "27498"), 0.0)
                ));

        when(dimensionRepository.readAll()).thenReturn(Arrays.asList(
                new Dimension("after", stringSchema(), ">", ""),
                new Dimension("before", stringSchema(), "<", ""),
                new Dimension("country", stringSchema(), "=", ""),
                new Dimension("postal-code", stringSchema(), "=", "")));

        when(relationService.readAll()).thenReturn(singletonList(new Equality()));
    }

    private ObjectNode stringSchema() {
        return new ObjectNode(JsonNodeFactory.instance).put("type", "string");
    }

    @Test
    public void shouldRead() {
        assertThat(unit.read("tax-rate", ImmutableMap.of("country", "DE")).getValue(), is(0.2));
    }

    @Test
    public void shouldReadAll() {
        assertThat(unit.readAll("tax-rate", ImmutableMap.of()).getValues(), contains(
                new Value(ImmutableMap.of("country", "CH", "after", "2017-01-01T00:00:00Z"), 0.08),
                new Value(ImmutableMap.of("country", "DE", "after", "2017-01-01T00:00:00Z"), 0.2),
                new Value(ImmutableMap.of("country", "CH", "before", "2015-01-01T00:00:00Z"), 0.06),
                new Value(ImmutableMap.of("country", "DE", "postal-code", "27498"), 0.0),
                new Value(ImmutableMap.of("country", "CH"), 0.07),
                new Value(ImmutableMap.of("country", "DE"), 0.19),
                new Value(ImmutableMap.of(), 0.25)));
    }

}
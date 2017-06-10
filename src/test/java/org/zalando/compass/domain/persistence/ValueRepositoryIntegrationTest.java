package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueId;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.databind.node.BooleanNode.FALSE;
import static com.fasterxml.jackson.databind.node.BooleanNode.TRUE;
import static com.google.common.collect.ImmutableMap.of;
import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;
import static org.zalando.compass.library.Schema.schema;
import static org.zalando.compass.library.Schema.stringSchema;

// TODO migrate to scenarios
@RunWith(SpringRunner.class)
@SpringBootTest
@Component
@Transactional
public class ValueRepositoryIntegrationTest {

    @TestConfiguration
    @Import({
            ValueRepository.class,
            RepositoryConfiguration.class
    })
    static class Configuration {

    }

    @Autowired
    private KeyRepository keys;

    @Autowired
    private DimensionRepository dimensions;

    @Autowired
    private ValueRepository unit;

    @Test
    public void shouldNotFind() throws IOException {
        assertThat(unit.findAll(byKey("foo")), is(emptyList()));
    }

    @Test
    public void shouldFindWithoutDimensions() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        unit.create(new Value("one", of(), TRUE));
        unit.create(new Value("two", of(), TRUE));

        final List<Value> values = unit.findAll(byKey("one"));
        assertThat(values, hasSize(1));

        final Value value = values.get(0);
        assertThat(value.getValue(), is(TRUE));
        assertThat(value.getDimensions(), is(emptyMap()));
    }

    @Test
    public void shouldFindWithDimensions() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        dimensions.create(new Dimension("foo", stringSchema(), "=", ""));
        dimensions.create(new Dimension("bar", stringSchema(), "=", ""));
        unit.create(new Value("one", of("foo", new TextNode("bar"), "bar", new TextNode("buz")), FALSE));
        unit.create(new Value("two", of(), TRUE));

        final List<Value> values = unit.findAll(byKey("one"));
        assertThat(values, hasSize(1));

        final Value value = values.get(0);
        assertThat(value.getValue(), is(FALSE));
        assertThat(value.getDimensions().values(), hasSize(2));
        assertThat(value.getDimensions(), hasEntry("foo", new TextNode("bar")));
        assertThat(value.getDimensions(), hasEntry("bar", new TextNode("buz")));
    }

    @Test
    public void shouldDelete() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        dimensions.create(new Dimension("foo", schema("boolean"), "=", ""));
        dimensions.create(new Dimension("bar", schema("number"), "=", ""));
        unit.create(new Value("one", of("foo", TRUE, "bar", new DecimalNode(ONE)), FALSE));
        unit.create(new Value("one", of("foo", FALSE), TRUE));
        unit.create(new Value("two", of(), TRUE));

        assertThat(unit.findAll(byKey("one")), hasSize(2));
        assertThat(unit.findAll(byKey("two")), hasSize(1));

        unit.delete(new ValueId("one", of("foo", TRUE, "bar", new DecimalNode(ONE))));
        assertThat(unit.findAll(byKey("one")), hasSize(1));
        assertThat(unit.findAll(byKey("two")), hasSize(1));

        unit.delete(new ValueId("one", of("foo", FALSE)));
        assertThat(unit.findAll(byKey("one")), is(empty()));
        assertThat(unit.findAll(byKey("two")), hasSize(1));

        unit.delete(new ValueId("two", of()));
        assertThat(unit.findAll(byKey("two")), is(empty()));
    }

    @Test
    public void shouldNotDelete() {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        dimensions.create(new Dimension("foo", schema("boolean"), "=", ""));
        unit.create(new Value("one", of("foo", FALSE), TRUE));

        assertThat(unit.findAll(byKey("one")), hasSize(1));

        final boolean deleted = unit.delete(new ValueId("one", of("unknown", new TextNode("foo"))));

        assertThat(deleted, is(false));
    }

    @Test
    public void shouldNotDeleteWithoutDimensions() {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        unit.create(new Value("one", of(), TRUE));

        assertThat(unit.findAll(byKey("one")), hasSize(1));

        final boolean deleted = unit.delete(new ValueId("one", of("unknown", new TextNode("foo"))));

        assertThat(deleted, is(false));
    }

}
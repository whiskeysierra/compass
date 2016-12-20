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
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.databind.node.BooleanNode.FALSE;
import static com.fasterxml.jackson.databind.node.BooleanNode.TRUE;
import static com.google.common.collect.ImmutableMap.of;
import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
    private ValueRepository unit;

    @Test
    public void shouldNotFind() throws IOException {
        assertThat(unit.readAll("foo"), is(emptyList()));
    }

    @Test
    public void shouldFindWithoutDimensions() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        unit.createOrUpdate("one", singleton(new Value(of(), TRUE)));
        unit.createOrUpdate("two", singleton(new Value(of(), TRUE)));

        final List<Value> values = unit.readAll("one");
        assertThat(values, hasSize(1));

        final Value value = values.get(0);
        assertThat(value.getValue(), is(TRUE));
        assertThat(value.getDimensions(), is(emptyMap()));
    }

    @Test
    public void shouldFindWithDimensions() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        unit.createOrUpdate("one", singleton(new Value(of("foo", new TextNode("bar"), "bar", new TextNode("buz")), FALSE)));
        unit.createOrUpdate("two", singleton(new Value(of(), TRUE)));

        final List<Value> values = unit.readAll("one");
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
        unit.createOrUpdate("one", singleton(new Value(of("foo", TRUE, "bar", new DecimalNode(ONE)), FALSE)));
        unit.createOrUpdate("two", singleton(new Value(of(), TRUE)));

        unit.delete("one", of("foo", "true"));
        assertThat(unit.readAll("one"), is(empty()));

        unit.delete("two", of());
        assertThat(unit.readAll("two"), is(empty()));
    }

}
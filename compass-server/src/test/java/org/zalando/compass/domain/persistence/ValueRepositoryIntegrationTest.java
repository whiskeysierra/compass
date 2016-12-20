package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;
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
        unit.create("one", singleton(new Value(ImmutableMap.of(), BooleanNode.TRUE)));
        unit.create("two", singleton(new Value(ImmutableMap.of(), BooleanNode.TRUE)));

        final List<Value> values = unit.readAll("one");
        assertThat(values, hasSize(1));

        final Value value = values.get(0);
        assertThat(value.getValue(), is(true));
        assertThat(value.getDimensions(), is(emptyMap()));
    }

    @Test
    public void shouldFindWithDimensions() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        unit.create("one", singleton(new Value(ImmutableMap.of("foo", new TextNode("bar"), "bar", new TextNode("buz")), BooleanNode.FALSE)));
        unit.create("two", singleton(new Value(ImmutableMap.of(), BooleanNode.TRUE)));

        final List<Value> values = unit.readAll("one");
        assertThat(values, hasSize(1));

        final Value value = values.get(0);
        assertThat(value.getValue(), is(false));
        assertThat(value.getDimensions().values(), hasSize(2));
        assertThat(value.getDimensions(), hasEntry("foo", "bar"));
        assertThat(value.getDimensions(), hasEntry("bar", "buz"));
    }

    @Test
    public void shouldDelete() throws IOException {
        keys.create(new Key("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new Key("two", new ObjectNode(JsonNodeFactory.instance), ""));
        unit.create("one", singleton(new Value(ImmutableMap.of("foo", new TextNode("bar"), "bar", new TextNode("buz")), BooleanNode.FALSE)));
        unit.create("two", singleton(new Value(ImmutableMap.of(), BooleanNode.TRUE)));

        unit.delete("one", ImmutableMap.of("foo", "bar"));
        assertThat(unit.readAll("one"), is(empty()));

        unit.delete("two", ImmutableMap.of());
        assertThat(unit.readAll("two"), is(empty()));
    }

}
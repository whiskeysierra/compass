package org.zalando.compass.domain.persistence;

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
import org.zalando.compass.domain.model.Realization;
import org.zalando.compass.domain.persistence.model.tables.pojos.KeyRow;
import org.zalando.compass.domain.persistence.model.tables.pojos.ValueRow;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.databind.node.BooleanNode.FALSE;
import static com.fasterxml.jackson.databind.node.BooleanNode.TRUE;
import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

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
        assertThat(unit.findAll(byKey("foo")), is(emptyList()));
    }

    @Test
    public void shouldFindWithoutDimensions() throws IOException {
        keys.create(new KeyRow("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new KeyRow("two", new ObjectNode(JsonNodeFactory.instance), ""));

        final List<ValueRow> values = unit.findAll(byKey("one"));
        assertThat(values, hasSize(1));

        final ValueRow value = values.get(0);
        assertThat(value.getValue(), is(TRUE));
        assertThat(value.getDimensions(), is(emptyMap()));
    }

    @Test
    public void shouldFindWithDimensions() throws IOException {
        keys.create(new KeyRow("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new KeyRow("two", new ObjectNode(JsonNodeFactory.instance), ""));

        final List<ValueRow> values = unit.findAll(byKey("one"));
        assertThat(values, hasSize(1));

        final ValueRow value = values.get(0);
        assertThat(value.getValue(), is(FALSE));
    }

    @Test
    public void shouldDelete() throws IOException {
        keys.create(new KeyRow("one", new ObjectNode(JsonNodeFactory.instance), ""));
        keys.create(new KeyRow("two", new ObjectNode(JsonNodeFactory.instance), ""));

        unit.delete(new Realization("one", of("foo", new TextNode("true"), "bar", new TextNode("1"))));
        assertThat(unit.findAll(byKey("one")), is(empty()));

        unit.delete(new Realization("two", of()));
        assertThat(unit.findAll(byKey("two")), is(empty()));
    }

}
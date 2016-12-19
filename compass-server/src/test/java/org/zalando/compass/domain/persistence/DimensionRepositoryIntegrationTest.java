package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.library.JacksonConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
@Transactional
public class DimensionRepositoryIntegrationTest {

    @TestConfiguration
    @ImportAutoConfiguration({
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            FlywayAutoConfiguration.class,
            TransactionAutoConfiguration.class,
    })
    @Import({
            DimensionRepository.class,
            JacksonConfiguration.class,
            EmbeddedDataSourceConfiguration.class,
            JdbcTemplateAutoConfiguration.class, // not used as an auto configuration
    })
    static class Configuration {

    }

    @Autowired
    private DimensionRepository unit;

    @Test
    public void shouldNotFind() throws IOException {
        assertThat(unit.get(emptySet()), is(emptyList()));
    }

    @Test
    public void shouldFindOnly() throws IOException {
        unit.create(new Dimension("country", new ObjectNode(instance).put("type", "string"),
                "=", "ISO 3166-1 alpha-2 country code"));

        final List<Dimension> dimensions = unit.get(singleton("country"));
        assertThat(dimensions, hasSize(1));

        final Dimension dimension = dimensions.get(0);
        assertThat(dimension.getId(), is("country"));
        assertThat(dimension.getSchema().size(), is(1));
        assertThat(dimension.getSchema().get("type").asText(), is("string"));
        assertThat(dimension.getRelation(), is("="));
        assertThat(dimension.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldFindTwoOutOfThree() throws IOException {
        unit.create(new Dimension("country", new ObjectNode(instance).put("type", "string"),
                "=", "ISO 3166-1 alpha-2 country code"));

        unit.create(new Dimension("sales-channel", new ObjectNode(instance).put("type", "string"),
                "=", "A sales channel..."));

        unit.create(new Dimension("locale", new ObjectNode(instance).put("type", "string"),
                "=", "Language"));

        final List<Dimension> dimensions = unit.get(newHashSet("country", "sales-channel"));
        assertThat(dimensions, hasSize(2));

        assertThat(dimensions.stream().map(Dimension::getId).collect(toSet()),
                is(newHashSet("country", "sales-channel")));
    }


}
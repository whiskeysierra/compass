package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.library.JacksonConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
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
    private JdbcTemplate template;

    @Autowired
    private DimensionRepository unit;

    @Before
    public void setUp() throws Exception {
        template.execute("SELECT SETVAL('dimension_priority_seq', 1)");
    }

    @Test
    public void shouldCreate() throws IOException {
        assertThat(createDimension(), is(true));
    }

    @Test
    public void shouldNotCreateTwice() throws IOException {
        assertThat(createDimension(), is(true));
        assertThat(createDimension(), is(false));
    }

    @Test
    public void shouldUpdate() throws IOException {
        createDimension();

        unit.update(new Dimension("country", new ObjectNode(instance).put("type", "integer"),
                "=", "Country ID"));

        final Dimension dimension = unit.read(singleton("country")).get(0);

        assertThat(dimension.getSchema().get("type").asText(), is("integer"));
        assertThat(dimension.getDescription(), is("Country ID"));
    }

    @Test
    public void shouldReorder() throws IOException {
        createDimension(newCountry());
        createDimension(newSalesChannel());
        createDimension(newLocale());

        final List<Dimension> before = unit.readAll();
        assertThat(before.stream().map(Dimension::getId).collect(toList()),
                contains("country", "sales-channel", "locale"));

        unit.reorder(Arrays.asList("sales-channel", "locale", "country"));

        final List<Dimension> after = unit.readAll();
        assertThat(after.stream().map(Dimension::getId).collect(toList()),
                contains("sales-channel", "locale", "country"));
    }

    @Test(expected = DuplicateKeyException.class)
    public void shouldFailToReorderIfNonUniquePriority() throws IOException {
        createDimension(newCountry());
        createDimension(newSalesChannel());
        createDimension(newLocale());

        unit.reorder(Arrays.asList("sales-channel", "locale"));
    }

    @Test
    public void shouldRead() throws IOException {
        createDimension();

        final List<Dimension> dimensions = unit.read(singleton("country"));
        assertThat(dimensions, hasSize(1));

        final Dimension dimension = dimensions.get(0);
        assertThat(dimension.getId(), is("country"));
        assertThat(dimension.getSchema().size(), is(1));
        assertThat(dimension.getSchema().get("type").asText(), is("string"));
        assertThat(dimension.getRelation(), is("="));
        assertThat(dimension.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldNotRead() throws IOException {
        assertThat(unit.read(emptySet()), is(emptyList()));
    }

    @Test
    public void shouldReadAll() throws IOException {
        createDimension(newCountry());
        createDimension(newSalesChannel());
        createDimension(newLocale());

        assertThat(unit.readAll(), contains(newCountry(), newSalesChannel(), newLocale()));
    }

    @Test
    public void shouldFindTwoOutOfThree() throws IOException {
        createDimension(newCountry());
        createDimension(newSalesChannel());
        createDimension(newLocale());

        final List<Dimension> dimensions = unit.read(newHashSet("country", "sales-channel"));
        assertThat(dimensions.stream().map(Dimension::getId).collect(toList()), contains("country", "sales-channel"));
    }

    @Test
    public void shouldDelete() throws IOException {
        createDimension(newCountry());

        assertThat(unit.delete("country"), is(true));
        assertThat(unit.read(singleton("country")), is(empty()));
    }

    private boolean createDimension() throws IOException {
        return createDimension(newCountry());
    }

    private Dimension newCountry() {
        return new Dimension("country", new ObjectNode(instance).put("type", "string"),
                "=", "ISO 3166-1 alpha-2 country code");
    }

    private Dimension newSalesChannel() {
        return new Dimension("sales-channel", new ObjectNode(instance).put("type", "string"),
                "=", "A sales channel...");
    }

    private Dimension newLocale() {
        return new Dimension("locale", new ObjectNode(instance).put("type", "string"),
                "=", "Language");
    }

    private boolean createDimension(final Dimension dimension) throws IOException {
        return unit.create(dimension);
    }

}
package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.persistence.model.tables.pojos.DimensionRow;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.domain.persistence.DimensionCriteria.dimensions;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
@Transactional
public class DimensionRepositoryIntegrationTest {

    @TestConfiguration
    @Import({
            DimensionRepository.class,
            RepositoryConfiguration.class,
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
        assertThat(create(), is(true));
    }

    @Test
    public void shouldNotCreateTwice() throws IOException {
        assertThat(create(), is(true));
        assertThat(create(), is(false));
    }

    @Test
    public void shouldUpdate() throws IOException {
        create();

        unit.update(new DimensionRow("country", null, new ObjectNode(instance).put("type", "integer"),
                "=", "Country ID"));

        final DimensionRow dimension = unit.read("country");

        assertThat(dimension.getSchema().get("type").asText(), is("integer"));
        assertThat(dimension.getDescription(), is("Country ID"));
    }

    @Test
    public void shouldReorder() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        final List<DimensionRow> before = unit.findAll();
        assertThat(before.stream().map(DimensionRow::getId).collect(toList()),
                contains("country", "sales-channel", "locale"));

        unit.reorder(Arrays.asList("sales-channel", "locale", "country"));

        final List<DimensionRow> after = unit.findAll();
        assertThat(after.stream().map(DimensionRow::getId).collect(toList()),
                contains("sales-channel", "locale", "country"));
    }

    @Test(expected = DuplicateKeyException.class)
    public void shouldFailToReorderIfNonUniquePriority() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        unit.reorder(Arrays.asList("sales-channel", "locale"));
    }

    @Test
    public void shouldRead() throws IOException {
        create();

        final DimensionRow dimension = unit.read("country");

        assertThat(dimension.getId(), is("country"));
        assertThat(dimension.getSchema().size(), is(1));
        assertThat(dimension.getSchema().get("type").asText(), is("string"));
        assertThat(dimension.getRelation(), is("="));
        assertThat(dimension.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldNotRead() throws IOException {
        assertThat(unit.findAll(dimensions(emptySet())), is(emptyList()));
    }

    @Test
    public void shouldReadAll() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        final List<String> dimensions = unit.findAll().stream()
                .map(DimensionRow::getId)
                .collect(toList());

        assertThat(dimensions, contains("country", "sales-channel", "locale"));
    }

    @Test
    public void shouldFindTwoOutOfThree() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        final List<DimensionRow> dimensions = unit.findAll(dimensions(newHashSet("country", "sales-channel")));
        assertThat(dimensions.stream().map(DimensionRow::getId).collect(toList()), contains("country", "sales-channel"));
    }

    @Test
    public void shouldDelete() throws IOException {
        create(newCountry());

        unit.delete("country");
        assertThat(unit.find("country"), is(empty()));
    }

    private boolean create() throws IOException {
        return create(newCountry());
    }

    private DimensionRow newCountry() {
        return new DimensionRow("country", null, new ObjectNode(instance).put("type", "string"),
                "=", "ISO 3166-1 alpha-2 country code");
    }

    private DimensionRow newSalesChannel() {
        return new DimensionRow("sales-channel", null, new ObjectNode(instance).put("type", "string"),
                "=", "A sales channel...");
    }

    private DimensionRow newLocale() {
        return new DimensionRow("locale", null, new ObjectNode(instance).put("type", "string"),
                "=", "Language");
    }

    private boolean create(final DimensionRow dimension) throws IOException {
        return unit.create(dimension);
    }

}
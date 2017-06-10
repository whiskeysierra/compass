package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

// TODO migrate to scenarios
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
    private DimensionRepository unit;

    @Test
    public void shouldCreate() throws IOException {
        create();

        assertThat(unit.find("country"), is(not(empty())));
    }

    @Test(expected = DuplicateKeyException.class)
    public void shouldNotCreateTwice() throws IOException {
        create();
        create();
    }

    @Test
    public void shouldUpdate() throws IOException {
        create();

        unit.update(new Dimension("country", new ObjectNode(instance).put("type", "integer"),
                "=", "Country ID"));

        final Dimension dimension = unit.find("country").orElseThrow(AssertionError::new);

        assertThat(dimension.getSchema().get("type").asText(), is("integer"));
        assertThat(dimension.getDescription(), is("Country ID"));
    }

    @Test
    public void shouldRead() throws IOException {
        create();

        final Dimension dimension = unit.find("country").orElseThrow(AssertionError::new);

        assertThat(dimension.getId(), is("country"));
        assertThat(dimension.getSchema().size(), is(1));
        assertThat(dimension.getSchema().get("type").asText(), is("string"));
        assertThat(dimension.getRelation(), is("="));
        assertThat(dimension.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldNotRead() throws IOException {
        assertThat(unit.findAll(emptySet()), is(emptyList()));
    }

    @Test
    public void shouldReadAll() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        final List<String> dimensions = unit.findAll().stream()
                .map(Dimension::getId)
                .collect(toList());

        // ordered by id
        assertThat(dimensions, contains("country", "locale", "sales-channel"));
    }

    @Test
    public void shouldFindTwoOutOfThree() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        final List<Dimension> dimensions = unit.findAll(newHashSet("country", "sales-channel"));
        assertThat(dimensions.stream().map(Dimension::getId).collect(toList()), contains("country", "sales-channel"));
    }

    @Test
    public void shouldDelete() throws IOException {
        create(newCountry());

        unit.delete("country");
        assertThat(unit.find("country"), is(empty()));
    }

    private void create() throws IOException {
        create(newCountry());
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

    private void create(final Dimension dimension) throws IOException {
        unit.create(dimension);
    }

}
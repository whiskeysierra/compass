package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Key;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.domain.persistence.KeyCriteria.keys;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
@Transactional
public class KeyRepositoryIntegrationTest {

    @TestConfiguration
    @Import({
            KeyRepository.class,
            RepositoryConfiguration.class
    })
    static class Configuration {

    }

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private KeyRepository unit;

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

        unit.update(new Key("country", new ObjectNode(instance).put("type", "integer"),
                "Country ID"));

        final Key key = unit.read("country");

        assertThat(key.getSchema().get("type").asText(), is("integer"));
        assertThat(key.getDescription(), is("Country ID"));
    }

    @Test
    public void shouldRead() throws IOException {
        create();

        final Key key = unit.read("country");

        assertThat(key.getId(), is("country"));
        assertThat(key.getSchema().size(), is(1));
        assertThat(key.getSchema().get("type").asText(), is("string"));
        assertThat(key.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldNotRead() throws IOException {
        assertThat(unit.findAll(keys(emptySet())), is(emptyList()));
    }

    @Test
    public void shouldReadAll() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        assertThat(unit.findAll(), contains(newCountry(), newLocale(), newSalesChannel()));
    }

    @Test
    public void shouldFindTwoOutOfThree() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        final List<Key> keys = unit.findAll(keys(newHashSet("country", "sales-channel")));
        assertThat(keys.stream().map(Key::getId).collect(toList()), contains("country", "sales-channel"));
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

    private Key newCountry() {
        return new Key("country", new ObjectNode(instance).put("type", "string"),
                "ISO 3166-1 alpha-2 country code");
    }

    private Key newSalesChannel() {
        return new Key("sales-channel", new ObjectNode(instance).put("type", "string"),
                "A sales channel...");
    }

    private Key newLocale() {
        return new Key("locale", new ObjectNode(instance).put("type", "string"),
                "Language");
    }

    private boolean create(final Key Key) throws IOException {
        return unit.create(Key);
    }

}
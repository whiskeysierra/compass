package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.library.JacksonConfiguration;

import java.io.IOException;
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
public class KeyRepositoryIntegrationTest {

    @TestConfiguration
    @ImportAutoConfiguration({
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            FlywayAutoConfiguration.class,
            TransactionAutoConfiguration.class,
    })
    @Import({
            KeyRepository.class,
            JacksonConfiguration.class,
            EmbeddedDataSourceConfiguration.class,
            JdbcTemplateAutoConfiguration.class, // not used as an auto configuration
    })
    static class Configuration {

    }

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private KeyRepository unit;

    @Test
    public void shouldCreate() throws IOException {
        assertThat(createKey(), is(true));
    }

    @Test
    public void shouldNotCreateTwice() throws IOException {
        assertThat(createKey(), is(true));
        assertThat(createKey(), is(false));
    }

    @Test
    public void shouldUpdate() throws IOException {
        createKey();

        unit.update(new Key("country", new ObjectNode(instance).put("type", "integer"),
                "Country ID"));

        final Key Key = unit.read(singleton("country")).get(0);

        assertThat(Key.getSchema().get("type").asText(), is("integer"));
        assertThat(Key.getDescription(), is("Country ID"));
    }

    @Test
    public void shouldRead() throws IOException {
        createKey();

        final List<Key> Keys = unit.read(singleton("country"));
        assertThat(Keys, hasSize(1));

        final Key Key = Keys.get(0);
        assertThat(Key.getId(), is("country"));
        assertThat(Key.getSchema().size(), is(1));
        assertThat(Key.getSchema().get("type").asText(), is("string"));
        assertThat(Key.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldNotRead() throws IOException {
        assertThat(unit.read(emptySet()), is(emptyList()));
    }

    @Test
    public void shouldReadAll() throws IOException {
        createKey(newCountry());
        createKey(newSalesChannel());
        createKey(newLocale());

        assertThat(unit.readAll(), contains(newCountry(), newLocale(), newSalesChannel()));
    }

    @Test
    public void shouldFindTwoOutOfThree() throws IOException {
        createKey(newCountry());
        createKey(newSalesChannel());
        createKey(newLocale());

        final List<Key> Keys = unit.read(newHashSet("country", "sales-channel"));
        assertThat(Keys.stream().map(Key::getId).collect(toList()), contains("country", "sales-channel"));
    }

    @Test
    public void shouldDelete() throws IOException {
        createKey(newCountry());

        assertThat(unit.delete("country"), is(true));
        assertThat(unit.read(singleton("country")), is(empty()));
    }

    private boolean createKey() throws IOException {
        return createKey(newCountry());
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

    private boolean createKey(final Key Key) throws IOException {
        return unit.create(Key);
    }

}
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
import org.zalando.compass.domain.model.Key;

import java.io.IOException;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

// TODO migrate to scenarios
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
    private KeyRepository unit;

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

        unit.update(new Key("country", new ObjectNode(instance).put("type", "integer"),
                "Country ID"));

        final Key key = unit.find("country").orElseThrow(AssertionError::new);

        assertThat(key.getSchema().get("type").asText(), is("integer"));
        assertThat(key.getDescription(), is("Country ID"));
    }

    @Test
    public void shouldRead() throws IOException {
        create();

        final Key key = unit.find("country").orElseThrow(AssertionError::new);

        assertThat(key.getId(), is("country"));
        assertThat(key.getSchema().size(), is(1));
        assertThat(key.getSchema().get("type").asText(), is("string"));
        assertThat(key.getDescription(), is("ISO 3166-1 alpha-2 country code"));
    }

    @Test
    public void shouldReadAll() throws IOException {
        create(newCountry());
        create(newSalesChannel());
        create(newLocale());

        assertThat(unit.findAll(), contains(newCountry(), newLocale(), newSalesChannel()));
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

    private void create(final Key Key) throws IOException {
        unit.create(Key);
    }

}
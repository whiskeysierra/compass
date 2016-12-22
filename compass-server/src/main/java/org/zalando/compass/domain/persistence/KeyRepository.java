package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gag.annotation.remark.Hack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.zalando.fauxpas.FauxPas.throwingBiFunction;

@Component
public class KeyRepository implements Repository<Key, String, KeyCriteria> {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public KeyRepository(final NamedParameterJdbcTemplate template, final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @Override
    public boolean create(final Key key) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", key.getId(),
                "schema", mapper.writeValueAsString(key.getSchema()),
                "description", key.getDescription());

        try {
            return template.update("" +
                    "INSERT INTO key (id, schema, description)" +
                    "VALUES (:id, :schema::JSONB, :description)", params) > 0;
        } catch (final DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public boolean exists(final String key) {
        final ImmutableMap<String, String> params = ImmutableMap.of("key", key);

        return template.queryForObject("" +
                "SELECT EXISTS (SELECT 1 " +
                "                 FROM key" +
                "                WHERE id = :key)", params, boolean.class);
    }

    @Override
    public Optional<Key> find(final String id) throws IOException {
        @Nullable final Key key = singleResult(template.query("" +
                "  SELECT id, schema, description" +
                "    FROM key" +
                "   WHERE id = :id", ImmutableMap.of("id", id), mapRow()));

        return Optional.ofNullable(key);
    }

    @Override
    public List<Key> findAll() {
        return template.query("" +
                "  SELECT id, schema, description" +
                "    FROM key " +
                "ORDER BY id ASC", ImmutableMap.of(), mapRow());
    }

    @Override
    public List<Key> findAll(final KeyCriteria criteria) throws IOException {
        final Set<String> keys = criteria.getKeys();

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        final ImmutableMap<String, Object> params = ImmutableMap.of("keys", keys);

        return template.query("" +
                "  SELECT id, schema, description" +
                "    FROM key" +
                "   WHERE id IN (:keys)" +
                "ORDER BY id ASC", params, mapRow());
    }

    @Hack
    private RowMapper<Key> mapRow() {
        return throwingBiFunction(this::map)::apply;
    }

    private Key map(final ResultSet row, final int rowNum) throws Exception {
        return new Key(
                row.getString("id"),
                mapper.readTree(row.getBytes("schema")),
                row.getString("description"));
    }

    @Override
    public boolean update(final Key key) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", key.getId(),
                "schema", mapper.writeValueAsString(key.getSchema()),
                "description", key.getDescription());

        final int updates = template.update("" +
                "UPDATE key" +
                "   SET schema = :schema::JSONB," +
                "       description = :description" +
                " WHERE id = :id", params);

        return updates > 0;
    }

    @Override
    public void delete(final String key) {
        final int deletions = template.update("" +
                "DELETE" +
                "  FROM key" +
                " WHERE id = :id", ImmutableMap.of("id", key));

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}

package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gag.annotation.remark.Hack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Key;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.zalando.fauxpas.FauxPas.throwingBiFunction;

@Repository
public class KeyRepository {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public KeyRepository(final NamedParameterJdbcTemplate template, final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

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

    public List<Key> read(final Set<String> keys) {
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

    public List<Key> readAll() {
        return template.query("" +
                "  SELECT id, schema, description" +
                "    FROM key " +
                "ORDER BY id ASC", ImmutableMap.of(), mapRow());
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

    public void update(final Key key) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", key.getId(),
                "schema", mapper.writeValueAsString(key.getSchema()), // TODO force validation on update
                "description", key.getDescription());

        template.update("" +
                "UPDATE key" +
                "   SET schema = :schema::JSONB," +
                "       description = :description" +
                " WHERE id = :id", params);
    }

    public boolean delete(final String key) {
        return template.update("" +
                "DELETE" +
                "  FROM key" +
                " WHERE id = :id", ImmutableMap.of("id", key)) == 1;
    }

}

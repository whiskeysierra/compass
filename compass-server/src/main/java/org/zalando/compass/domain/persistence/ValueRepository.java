package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Value;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ValueRepository {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public ValueRepository(final NamedParameterJdbcTemplate template,
            final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @SneakyThrows
    public void create(final String key, final Value value) {
        final ImmutableMap<String, String> params = ImmutableMap.of(
                "key", key,
                "dimensions", mapper.writeValueAsString(value.getDimensions()),
                "value", mapper.writeValueAsString(value.getValue()));

        template.update("" +
                "INSERT INTO value (key, dimensions, value)" +
                "     VALUES (:key, :dimensions::JSONB, :value::JSONB)", params);
    }

    public List<Value> get(final String key) {
        final ImmutableMap<String, String> params = ImmutableMap.of("key", key);

        return template.query("" +
                "SELECT dimensions AS dimensions," +
                "       value" +
                "  FROM value" +
                " WHERE key = :key", params, this::map);
    }

    @SneakyThrows
    private Value map(final ResultSet row, @SuppressWarnings("unused") final int num) throws SQLException {
        final ImmutableMap<String, Object> dimensions = mapper.readValue(row.getBytes("dimensions"),
                new TypeReference<ImmutableMap<String, Object>>() {
                });
        final Object value = mapper.readValue(row.getBytes("value"), Object.class);

        return new Value(dimensions, value);
    }

}

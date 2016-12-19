package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Value;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public void create(final String key, final Value value) throws IOException {
        final Map<String, Object> dimensions = new LinkedHashMap<>(value.getDimensions());
        dimensions.put("_key", key);

        final ImmutableMap<String, String> params = ImmutableMap.of(
                "dimensions", mapper.writeValueAsString(dimensions),
                "value", mapper.writeValueAsString(value.getValue()));

        template.update("" +
                "INSERT INTO value (dimensions, value)" +
                "     VALUES (:dimensions::JSONB, :value::JSONB)", params);
    }

    public List<Value> get(final String key) throws IOException {
        final ImmutableMap<String, String> params = ImmutableMap.of("key", key);

        return template.query("" +
                "SELECT dimensions - '_key' AS dimensions," +
                "       value" +
                "  FROM value" +
                " WHERE dimensions->>'_key' = :key", params, this::map);
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

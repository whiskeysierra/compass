package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gag.annotation.remark.Hack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Value;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.zalando.fauxpas.FauxPas.throwingBiFunction;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

@Repository
public class ValueRepository {

    private static final TypeReference<ImmutableMap<String, JsonNode>> TYPE_REF =
            new TypeReference<ImmutableMap<String, JsonNode>>() {
            };

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public ValueRepository(final NamedParameterJdbcTemplate template,
            final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    public void createOrUpdate(final String key, final Collection<Value> values) {
        final SqlParameterSource[] params = values.stream()
                .map(throwingFunction(value -> new MapSqlParameterSource(ImmutableMap.of(
                        "key", key,
                        "dimensions", mapper.writeValueAsString(value.getDimensions()),
                        "value", mapper.writeValueAsString(value.getValue())))))
                .toArray(SqlParameterSource[]::new);

        template.batchUpdate("" +
                "INSERT INTO value (key, dimensions, value)" +
                "     VALUES (:key, :dimensions::JSONB, :value::JSONB)" +
                "ON CONFLICT (key, dimensions) DO UPDATE" +
                "        SET value = excluded.value", params);
    }

    public List<Value> readAllByKey(final String key) {
        final ImmutableMap<String, String> params = ImmutableMap.of("key", key);

        return template.query("" +
                "SELECT dimensions," +
                "       value" +
                "  FROM value" +
                " WHERE key = :key", params, mapRow());
    }

    public List<Value> readAllByKeyPattern(final String keyPattern) {
        final ImmutableMap<String, String> params = ImmutableMap.of("key", "%" + keyPattern + "%");

        return template.query("" +
                "SELECT dimensions," +
                "       value" +
                "  FROM value" +
                " WHERE key ILIKE :key", params, mapRow());
    }

    public List<Value> readAllByDimension(final String dimension) {
        final ImmutableMap<String, Object> params = ImmutableMap.of("dimension", dimension);

        return template.query("" +
                "SELECT dimensions," +
                "       value" +
                "  FROM value" +
                " WHERE dimensions ? :dimension", params, mapRow());
    }

    @Hack
    private RowMapper<Value> mapRow() {
        return throwingBiFunction(this::map)::apply;
    }

    private Value map(final ResultSet row, @SuppressWarnings("unused") final int num) throws Exception {
        return new Value(
                mapper.readValue(row.getBytes("dimensions"), TYPE_REF),
                mapper.readTree(row.getBytes("value")));
    }

    public void delete(final String key, final Map<String, String> filter) throws IOException {
        final ImmutableMap<String, String> params = ImmutableMap.of(
                "key", key,
                "dimensions", mapper.writeValueAsString(filter));

        template.update("" +
                "DELETE" +
                "  FROM value" +
                " WHERE key = :key" +
                // transform dimension values to text for comparison
                "   AND (SELECT COALESCE(JSONB_OBJECT_AGG(key, value), '{}'::JSONB) " +
                "          FROM JSONB_EACH_TEXT(dimensions)) @> :dimensions::JSONB", params);
    }

}

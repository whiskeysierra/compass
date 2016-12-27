package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gag.annotation.remark.Hack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Realization;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.of;
import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.zalando.compass.domain.persistence.ValueCriteria.withoutCriteria;
import static org.zalando.fauxpas.FauxPas.throwingBiFunction;

@Component
public class ValueRepository implements Repository<Value, Realization, ValueCriteria> {

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

    @Override
    public boolean create(final Value value) throws IOException {
        final ImmutableMap<String, Object> params = of(
                "key", value.getKey(),
                "dimensions", mapper.writeValueAsString(value.getDimensions()),
                "value", mapper.writeValueAsString(value.getValue())
        );

        final int updates = template.update("" +
                "INSERT INTO value (key, dimensions, value)" +
                "     VALUES (:key, :dimensions::JSONB, :value::JSONB)", params);

        return updates > 0;
    }

    @Override
    public Optional<Value> find(final Realization id) throws IOException {
        final ImmutableMap<String, String> params = of(
                "key", id.getKey(),
                "dimensions", mapper.writeValueAsString(id.getDimensions())
        );

        @Nullable final Value value = singleResult(template.query("" +
                "SELECT key," +
                "       dimensions," +
                "       value" +
                "  FROM value" +
                " WHERE key = :key" +
                "   AND dimensions = :dimensions::JSONB", params, mapRow()));

        return Optional.ofNullable(value);
    }

    @Override
    public List<Value> findAll() throws IOException {
        return findAll(withoutCriteria());
    }

    @Override
    public List<Value> findAll(final ValueCriteria criteria) throws IOException {
        if (criteria.getKey() != null) {
            return template.query("" +
                    "SELECT key," +
                    "       dimensions," +
                    "       value" +
                    "  FROM value" +
                    " WHERE key = :key", of("key", criteria.getKey()), mapRow());
        } else if (criteria.getKeyPattern() != null) {
            return template.query("" +
                    "SELECT key," +
                    "       dimensions," +
                    "       value" +
                    "  FROM value" +
                    " WHERE key ILIKE :key", of("key", "%" + criteria.getKeyPattern() + "%"), mapRow());
        } else if (criteria.getDimension() != null) {
            return template.query("" +
                    "SELECT key," +
                    "       dimensions," +
                    "       value" +
                    "  FROM value" +
                    " WHERE JSONB_EXISTS(dimensions ,:dimension)", of("dimension", criteria.getDimension()), mapRow());
        } else {
            return template.query("" +
                    "SELECT key," +
                    "       dimensions," +
                    "       value" +
                    "  FROM value" +
                    " WHERE JSONB_EXISTS(dimensions ,:dimension)", of(), mapRow());
        }
    }

    @Hack
    private RowMapper<Value> mapRow() {
        return throwingBiFunction(this::map)::apply;
    }

    private Value map(final ResultSet row, @SuppressWarnings("unused") final int num) throws Exception {
        return new Value(
                row.getString("key"),
                mapper.readValue(row.getBytes("dimensions"), TYPE_REF),
                mapper.readTree(row.getBytes("value")));
    }

    @Override
    public boolean update(final Value value) throws IOException {
        final ImmutableMap<String, Object> params = of(
                "key", value.getKey(),
                "dimensions", mapper.writeValueAsString(value.getDimensions()),
                "value", mapper.writeValueAsString(value.getValue()));

        final int updates = template.update("" +
                "UPDATE value" +
                "   SET value = :value::JSONB" +
                " WHERE key = :key" +
                "   AND dimensions = :dimensions::JSONB", params);

        return updates > 0;
    }

    @Override
    public void delete(final Realization id) throws IOException {
        final ImmutableMap<String, String> params = of(
                "key", id.getKey(),
                "dimensions", mapper.writeValueAsString(id.getDimensions()));

        // TODO document text value hack here!
        final int deletions = template.update("" +
                "DELETE" +
                "  FROM value" +
                " WHERE key = :key" +
                // transform dimension values to text for comparison
                "   AND (SELECT COALESCE(JSONB_OBJECT_AGG(key, value), '{}'::JSONB) " +
                "          FROM JSONB_EACH_TEXT(dimensions)) = :dimensions::JSONB", params);

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}

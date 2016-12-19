package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DimensionRepository {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public DimensionRepository(final NamedParameterJdbcTemplate template, final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @SneakyThrows
    public boolean create(final Dimension dimension) {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "relation", dimension.getRelation(),
                "description", dimension.getDescription());

        return template.update("" +
                "INSERT INTO dimension (id, schema, relation, description)" +
                "VALUES (:id, :schema::JSONB, :relation, :description)" +
                "ON CONFLICT DO NOTHING", params) > 0;
    }

    @SneakyThrows
    public void update(final Dimension dimension) {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "relation", dimension.getRelation(),
                "description", dimension.getDescription());

        template.update("" +
                "UPDATE dimension" +
                "   SET schema = :schema::JSONB," +
                "       relation = :relation," +
                "       description = :description" +
                " WHERE id = :id", params);
    }

    public void reorder(final Map<String, Integer> ranks) {
        template.update("" +
                "UPDATE dimension" +
                "   SET priority = next_priority" +
                "  FROM UNNEST(:ranks) AS (dimension_id, next_priority)" +
                " WHERE id = dimension_id", ImmutableMap.of("ranks", ranks.entrySet().stream()
        .map(e -> new Object[] {e.getKey(), e.getValue()}).toArray()));
    }

    public List<Dimension> get(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        final ImmutableMap<String, Object> params = ImmutableMap.of("dimensions", dimensions);

        return template.query("" +
                "SELECT id, schema, relation, description" +
                "  FROM dimension" +
                " WHERE id IN (:dimensions)", params, this::map);
    }

    public List<Dimension> getAll() {
        return template.query("" +
                "SELECT id, schema, relation, description" +
                "  FROM dimension", this::map);
    }

    @SneakyThrows
    private Dimension map(final ResultSet row, @SuppressWarnings("unused") final int rowNum) throws SQLException {
        return new Dimension(
                row.getString("id"),
                mapper.readTree(row.getBytes("schema")),
                row.getString("relation"),
                row.getString("description")
        );
    }

    public boolean delete(final String dimension) {
        return template.update("" +
                "DELETE" +
                "  FROM dimension" +
                " WHERE id = :id", ImmutableMap.of("id", dimension)) == 1;
    }
}

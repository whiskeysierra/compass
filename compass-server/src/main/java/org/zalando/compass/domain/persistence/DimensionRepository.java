package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
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

    public void create(final Dimension dimension) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "relation", dimension.getRelation(),
                "description", dimension.getDescription());

        template.update("" +
                "INSERT INTO dimension (id, schema, relation, description)" +
                "     VALUES (:id, :schema::JSONB, :relation, :description)", params);
    }

    public List<Dimension> get(final Set<String> dimensions) throws IOException {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        final MapSqlParameterSource params = new MapSqlParameterSource("dimensions", dimensions);

        return template.query("" +
                "SELECT id, schema, relation, description" +
                "  FROM dimension" +
                " WHERE id IN (:dimensions)", params, this::map);
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

}

package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gag.annotation.remark.Hack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static org.zalando.fauxpas.FauxPas.throwingBiFunction;

@Repository
public class DimensionRepository {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public DimensionRepository(final NamedParameterJdbcTemplate template, final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    public boolean create(final Dimension dimension) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "relation", dimension.getRelation(),
                "description", dimension.getDescription());

        try {
            return template.update("" +
                    "INSERT INTO dimension (id, schema, relation, description)" +
                    "VALUES (:id, :schema::JSONB, :relation, :description)", params) > 0;
        } catch (final DuplicateKeyException e) {
            return false;
        }
    }

    public List<Dimension> read(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        final ImmutableMap<String, Object> params = ImmutableMap.of("dimensions", dimensions);

        return template.query("" +
                "  SELECT id, schema, relation, description" +
                "    FROM dimension" +
                "   WHERE id IN (:dimensions)" +
                "ORDER BY priority ASC", params, mapRow());
    }

    public List<Dimension> readAll() {
        return template.query("" +
                "  SELECT id, schema, relation, description" +
                "    FROM dimension " +
                "ORDER BY priority ASC", ImmutableMap.of(), mapRow());
    }

    @Hack
    private RowMapper<Dimension> mapRow() {
        return throwingBiFunction(this::map)::apply;
    }

    private Dimension map(final ResultSet row, @SuppressWarnings("unused") final int rowNum) throws Exception {
        return new Dimension(
                row.getString("id"),
                mapper.readTree(row.getBytes("schema")),
                row.getString("relation"),
                row.getString("description"));
    }

    public void update(final Dimension dimension) throws IOException {
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

    public void reorder(final List<String> dimensions) {
        final List<Object[]> ranks = new ArrayList<>(dimensions.size());
        final ListIterator<String> iterator = dimensions.listIterator();

        while (iterator.hasNext()) {
            ranks.add(new Object[]{iterator.next(), iterator.nextIndex()});
        }

        template.update("" +
                "UPDATE dimension" +
                "   SET priority = new_priority" +
                "  FROM (VALUES :ranks) AS ranks(dimension_id, new_priority)" +
                " WHERE id = dimension_id", ImmutableMap.of("ranks", ranks));
    }

    public boolean delete(final String dimension) {
        return template.update("" +
                "DELETE" +
                "  FROM dimension" +
                " WHERE id = :id", ImmutableMap.of("id", dimension)) == 1;
    }

}

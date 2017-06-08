package org.zalando.compass.domain.persistence;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Relation;

import java.util.List;
import java.util.Optional;

import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

@Component
public class RelationRepository implements Repository<Relation, String, Void> {

    private final ImmutableList<Relation> list = ImmutableList.copyOf(load(Relation.class));

    private final ImmutableMap<String, Relation> map = list.stream()
            .collect(collectingAndThen(toMap(Relation::getId, identity()), ImmutableMap::copyOf));

    @Override
    public boolean create(final Relation relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Relation> find(final String id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<Relation> findAll() {
        return list;
    }

    @Override
    public List<Relation> findAll(final Void criteria) {
        return list;
    }

    @Override
    public boolean update(final Relation relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException();
    }

}

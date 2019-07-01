package org.zalando.compass.infrastructure.relations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.spi.repository.RelationRepository;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.sortedCopyOf;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Comparator.comparing;
import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;

// TODO wrong package?!
// TODO find better package name
// TODO allow test usage without making it public
@Component
public class ServiceLoaderRelationRepository implements RelationRepository {

    private final ImmutableList<Relation> list = sortedCopyOf(comparing(Relation::getId), load(Relation.class));

    private final ImmutableMap<String, Relation> map = list.stream()
            .collect(toImmutableMap(Relation::getId, identity()));

    @Override
    public List<Relation> findAll() {
        return list;
    }

    @Override
    public Optional<Relation> find(final String id) {
        return Optional.ofNullable(map.get(id));
    }
}

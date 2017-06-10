package org.zalando.compass.domain.logic.relation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.persistence.NotFoundException;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.sortedCopyOf;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Comparator.comparing;
import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;

@Component
class DefaultRelationService implements RelationService {

    private final ImmutableList<Relation> list = sortedCopyOf(comparing(Relation::getId), load(Relation.class));

    private final ImmutableMap<String, Relation> map = list.stream()
            .collect(toImmutableMap(Relation::getId, identity()));

    @Override
    public Relation read(final String id) {
        return Optional.ofNullable(map.get(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Relation> readAll() {
        return list;
    }

}

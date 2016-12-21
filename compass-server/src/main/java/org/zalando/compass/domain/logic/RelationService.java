package org.zalando.compass.domain.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Relation;

import javax.annotation.Nullable;
import java.util.List;

import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

@Service
public class RelationService {

    private final ImmutableList<Relation> list = ImmutableList.copyOf(load(Relation.class));

    private final ImmutableMap<String, Relation> map = list.stream()
            .collect(collectingAndThen(toMap(Relation::getId, identity()), ImmutableMap::copyOf));

    public List<Relation> readAll() {
        return list;
    }

    @Nullable
    public Relation read(final String id) {
        return map.get(id);
    }

}

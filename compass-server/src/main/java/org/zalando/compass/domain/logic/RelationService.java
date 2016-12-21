package org.zalando.compass.domain.logic;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Relation;

import java.util.List;

import static java.util.ServiceLoader.load;

@Service
public class RelationService {

    private final ImmutableList<Relation> relations = ImmutableList.copyOf(load(Relation.class));

    public List<Relation> readAll() {
        return relations;
    }

}

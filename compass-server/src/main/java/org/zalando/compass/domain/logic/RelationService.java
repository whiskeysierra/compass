package org.zalando.compass.domain.logic;

import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.relations.Equality;
import org.zalando.compass.domain.logic.relations.GreaterThan;
import org.zalando.compass.domain.logic.relations.GreaterThanOrEqual;
import org.zalando.compass.domain.logic.relations.LessThan;
import org.zalando.compass.domain.logic.relations.LessThanOrEqual;
import org.zalando.compass.domain.logic.relations.Matches;
import org.zalando.compass.domain.logic.relations.Prefix;
import org.zalando.compass.domain.model.Relation;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

import static java.util.Arrays.asList;

@Service
@RequestScope
public class RelationService {

    public List<Relation> readAll() {
        return asList(new Equality(),
                new GreaterThan(), new GreaterThanOrEqual(),
                new LessThan(), new LessThanOrEqual(),
                new Matches(), new Prefix());
    }

}

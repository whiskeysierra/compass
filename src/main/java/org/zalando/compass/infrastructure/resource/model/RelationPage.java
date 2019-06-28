package org.zalando.compass.infrastructure.resource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Relation;

import java.util.List;

@Getter
@AllArgsConstructor
public final class RelationPage {

    private final List<Relation> relations;

}

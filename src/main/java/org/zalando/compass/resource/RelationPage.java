package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Relation;

import java.util.List;

@Getter
@AllArgsConstructor
final class RelationPage {

    private final List<Relation> relations;

}

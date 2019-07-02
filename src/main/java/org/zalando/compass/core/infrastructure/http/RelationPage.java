package org.zalando.compass.core.infrastructure.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.core.domain.model.Relation;

import java.util.List;

@Getter
@AllArgsConstructor
final class RelationPage {

    private final List<Relation> relations;

}

package org.zalando.compass.core.infrastructure.http.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.kernel.domain.model.Relation;

import java.util.List;

@Getter
@AllArgsConstructor
public final class RelationPage {

    private final List<Relation> relations;

}

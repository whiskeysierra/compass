package org.zalando.compass.core.domain.api;

import org.zalando.compass.core.domain.model.Relation;

import java.util.List;

public interface RelationService {

    List<Relation> readAll();

    Relation read(String id);

}

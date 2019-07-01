package org.zalando.compass.domain.api;

import org.zalando.compass.domain.model.Relation;

import java.util.List;

public interface RelationService {

    List<Relation> readAll();

    Relation read(String id);

}

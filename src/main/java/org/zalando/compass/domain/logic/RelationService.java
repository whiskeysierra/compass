package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Relation;

import java.util.List;

public interface RelationService {

    Relation read(String id);

    List<Relation> readAll();

}

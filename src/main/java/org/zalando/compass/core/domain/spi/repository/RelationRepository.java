package org.zalando.compass.core.domain.spi.repository;

import org.zalando.compass.core.domain.model.Relation;

import java.util.List;
import java.util.Optional;

public interface RelationRepository {

    List<Relation> findAll();

    Optional<Relation> find(String id);

}

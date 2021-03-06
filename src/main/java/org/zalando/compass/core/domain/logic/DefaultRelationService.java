package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.api.RelationService;
import org.zalando.compass.core.domain.model.Relation;
import org.zalando.compass.core.domain.spi.repository.RelationRepository;

import java.util.List;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultRelationService implements RelationService {

    private final RelationRepository repository;

    @Override
    public List<Relation> readAll() {
        return repository.findAll();
    }

    @Override
    public Relation read(final String id) {
        return repository.find(id)
                .orElseThrow(NotFoundException::new);
    }

}

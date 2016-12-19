package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.persistence.DimensionRepository;

@Service
public class KeyService {

    private final DimensionRepository repository;

    @Autowired
    public KeyService(final DimensionRepository repository) {
        this.repository = repository;
    }

}

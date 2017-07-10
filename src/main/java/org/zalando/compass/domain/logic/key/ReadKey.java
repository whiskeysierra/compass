package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.library.Pages;

import javax.annotation.Nullable;

@Component
class ReadKey {

    private final KeyRepository repository;

    @Autowired
    ReadKey(final KeyRepository repository) {
        this.repository = repository;
    }

    Page<Key> readPage(@Nullable final String term, final int limit, @Nullable final String after) {
        return Pages.page(repository.findAll(term, limit + 1, after), limit);
    }

    Key read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

}

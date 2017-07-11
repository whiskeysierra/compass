package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadKey {

    private final KeyRepository repository;

    @Autowired
    ReadKey(final KeyRepository repository) {
        this.repository = repository;
    }

    PageResult<Key> readPage(@Nullable final String term, final int limit, @Nullable final String after) {
        final PageQuery<String> query = PageQuery.create(after, null, limit);
        final List<Key> keys = repository.findAll(term, query.increment());
        return query.paginate(keys);
    }

    Key read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

}

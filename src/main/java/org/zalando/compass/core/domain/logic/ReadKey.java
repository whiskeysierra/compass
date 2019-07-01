package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.revision.domain.api.KeyRevisionService;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Revisioned;
import org.zalando.compass.core.domain.spi.repository.KeyRepository;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadKey {

    private final KeyRepository repository;
    private final KeyRevisionService revisionService;

    PageResult<Key> readPage(@Nullable final String term, final Pagination<String> query) {
        final List<Key> keys = repository.findAll(term, query.increment());
        return query.paginate(keys);
    }

    Key readOnly(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

    Revisioned<Key> read(final String id) {
        final Key key = readOnly(id);
        final Revision revision = readLatestRevision(id);
        return Revisioned.create(key, revision);
    }

    private Revision readLatestRevision(final String id) {
        // TODO don't use pagination for this, we're fetching one to much
        final Pagination<Long> pagination = Cursor.<Long, Void>initial().with(null, 1).paginate();
        return revisionService.readRevisions(id, pagination).getHead();
    }

}

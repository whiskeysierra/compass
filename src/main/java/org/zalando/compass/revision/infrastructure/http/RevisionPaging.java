package org.zalando.compass.revision.infrastructure.http;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.ResponseEntity;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.core.infrastructure.http.RevisionCollectionRepresentation;
import org.zalando.compass.core.infrastructure.http.RevisionRepresentation;
import org.zalando.fauxpas.ThrowingFunction;

import java.io.IOException;
import java.net.URI;

@VisibleForTesting
final class RevisionPaging {

    private RevisionPaging() {

    }

    static <Q> ResponseEntity<RevisionCollectionRepresentation> paginate(
            final PageResult<Revision> page,
            final Cursor<Long, Q> cursor,
            final ThrowingFunction<Cursor<Long, Q>, URI, IOException> pageLinker,
            final ThrowingFunction<Revision, URI, IOException> itemLinker) {

        final var representation = page.render(
                RevisionCollectionRepresentation::new,
                cursor,
                Revision::getId,
                pageLinker,
                revision ->
                        RevisionRepresentation.valueOf(revision, itemLinker.apply(revision)));

        return ResponseEntity.ok(representation);
    }

}

package org.zalando.compass.resource;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.ResponseEntity;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.fauxpas.ThrowingFunction;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@VisibleForTesting
public final class RevisionPaging {

    private RevisionPaging() {

    }

    static ResponseEntity<RevisionCollectionRepresentation> paginate(
            final PageResult<Revision> page,
            final ThrowingFunction<Revision, URI, IOException> next,
            final ThrowingFunction<Revision, URI, IOException> prev,
            final ThrowingFunction<Revision, URI, IOException> linker) {
        final List<RevisionRepresentation> revisions = page.getElements().stream()
                .map(revision -> new RevisionRepresentation(
                        revision.getId(),
                        revision.getTimestamp(),
                        linker.apply(revision),
                        revision.getType(),
                        revision.getUser(),
                        revision.getComment()
                ))
                .collect(toList());

        return ResponseEntity.ok(new RevisionCollectionRepresentation(
                // direction would be null if we wouldn't paginate already
                page.hasNext() ? next.apply(page.getTail()) : null,
                page.hasPrevious() ? prev.apply(page.getHead()) : null,
                revisions));
    }

}

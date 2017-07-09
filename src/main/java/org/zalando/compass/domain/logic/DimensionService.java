package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.util.List;

public interface DimensionService {

    boolean replace(Dimension dimension, @Nullable final String comment);

    // TODO Page
    List<Dimension> readPage(@Nullable String term);

    Dimension read(String id);

    Page<Revision> readPageRevisions(int limit, @Nullable Long after);

    PageRevision<Dimension> readPageAt(long revision);

    Page<Revision> readRevisions(String id, int limit, @Nullable Long after);

    DimensionRevision readAt(String id, long revision);

    void delete(String id, @Nullable final String comment);

}

package org.zalando.compass.revision.domain.api;

import org.zalando.compass.core.domain.model.Revision;

import javax.annotation.Nullable;

public interface RevisionService {

    Revision create(@Nullable String comment);
    Revision read(long revisionId);

}

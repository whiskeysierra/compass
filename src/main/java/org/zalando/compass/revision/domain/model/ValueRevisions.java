package org.zalando.compass.revision.domain.model;

import com.google.common.collect.ImmutableList;
import org.zalando.compass.core.domain.model.Matchable;
import org.zalando.compass.core.domain.model.Value;

@lombok.Value
public final class ValueRevisions implements Matchable<ValueRevision> {

    ImmutableList<ValueRevision> values;

}


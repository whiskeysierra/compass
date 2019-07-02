package org.zalando.compass.core.domain.model.event;

import com.google.common.collect.MapDifference.ValueDifference;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;

import java.util.Collection;

@lombok.Value
public final class ValuesReplaced {
    Key key;
    Collection<Value> creates;
    Collection<ValueDifference<Value>> updates;
    Collection<Value> deletes;
    String comment;
}

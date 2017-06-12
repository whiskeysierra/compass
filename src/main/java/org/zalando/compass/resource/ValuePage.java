package org.zalando.compass.resource;

import org.zalando.compass.domain.model.Value;

import java.util.List;

@lombok.Value
public final class ValuePage {

    List<Value> values;

}

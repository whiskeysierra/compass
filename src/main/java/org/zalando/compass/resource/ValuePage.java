package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Value;

import java.util.List;

@Getter
@AllArgsConstructor
class ValuePage {

    private final List<Value> values;

}

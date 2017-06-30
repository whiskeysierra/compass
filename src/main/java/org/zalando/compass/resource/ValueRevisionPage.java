package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.ValueRevision;

import java.util.List;

@Getter
@AllArgsConstructor
final class ValueRevisionPage {

    private final List<ValueRevision> values;

}

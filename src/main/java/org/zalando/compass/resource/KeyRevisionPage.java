package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.KeyRevision;

import java.util.List;

@Getter
@AllArgsConstructor
class KeyRevisionPage {

    private final Link next;
    private final List<KeyRevision> keys;

}

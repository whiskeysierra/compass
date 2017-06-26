package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Key;

import java.util.List;

@Getter
@AllArgsConstructor
class KeyPage {

    private final List<Key> keys;

}

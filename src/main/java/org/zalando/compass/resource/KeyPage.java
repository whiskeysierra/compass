package org.zalando.compass.resource;

import org.zalando.compass.domain.model.Key;

import java.util.List;

@lombok.Value
public class KeyPage {

    private final List<Key> keys;

}

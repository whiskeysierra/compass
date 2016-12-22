package org.zalando.compass.domain.persistence;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class KeyCriteria {

    private Set<String> keys;

    public static KeyCriteria keys(final Set<String> keys) {
        return new KeyCriteria(keys);
    }

}

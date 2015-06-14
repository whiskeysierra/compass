package org.zalando.compass.api;

/*
 * ⁣​
 * Compass API
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;

@Immutable // TODO well, it really depends on the value of T
public final class Node<T> {

    private final Optional<URI> dimension;
    private final ImmutableMap<String, Node<T>> values;
    private final Optional<T> value;

    public Node(Optional<URI> dimension, @Nullable ImmutableMap<String, Node<T>> values, Optional<T> value) {
        this.dimension = Objects.requireNonNull(dimension, "Dimension");
        // an unfortunate leak of implementation details, since jackson can't deserialize null as an empty map
        this.values = firstNonNull(values, ImmutableMap.<String, Node<T>>of());
        this.value = Objects.requireNonNull(value, "Value");
    }

    public Optional<URI> getDimension() {
        return dimension;
    }

    public ImmutableMap<String, Node<T>> getValues() {
        return values;
    }

    public Optional<T> getValue() {
        return value;
    }

}

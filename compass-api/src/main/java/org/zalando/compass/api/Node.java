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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.lang.String.format;

@Immutable
public final class Node<T> {

    private final Optional<Dimension> dimension;
    private final ImmutableMap<String, Node<T>> values;
    private final Optional<T> value;

    private Node(Optional<Dimension> dimension, @Nullable ImmutableMap<String, Node<T>> values, Optional<T> value) {
        this.dimension = firstNonNull(dimension, Optional.<Dimension>empty());
        this.values = firstNonNull(values, ImmutableMap.<String, Node<T>>of());
        this.value = firstNonNull(value, Optional.<T>empty());
    }

    public Optional<Dimension> getDimension() {
        return dimension;
    }

    public ImmutableMap<String, Node<T>> getValues() {
        return values;
    }

    public Optional<T> getValue() {
        return value;
    }
    
    private boolean isBranch() {
        return dimension.isPresent() && !values.isEmpty();
    }
    
    private boolean isLeaf() {
        return value.isPresent();
    }

    @Override
    public String toString() {
        if (isBranch() && isLeaf()) {
            return format("{%s:{%s}, default:%s}", dimension.get(),
                    Joiner.on(", ").withKeyValueSeparator(":").join(values), value.get());
        } else if (isBranch()) {
            return format("{%s:{%s}}", dimension.get(),
                    Joiner.on(", ").withKeyValueSeparator(":").join(values));
        } else if (isLeaf()) {
            return value.get().toString();
        } else {
            return "foo";
        }
    }
    
    public static <T> Node<T> valueOf(@Nonnull T value) {
        return new Node<>(null, null, Optional.of(value));
    }
    
    public static <T> Node<T> valueOf(@Nonnull Optional<T> value) {
        return new Node<>(null, null, value);
    }
    
    public static <T> Node<T> of(@Nonnull Dimension dimension, @Nonnull ImmutableMap<String, Node<T>> values) {
        return new Node<>(Optional.of(dimension), values, Optional.empty());
    }
    
    public static <T> Node<T> of(@Nonnull Dimension dimension, @Nonnull ImmutableMap<String, Node<T>> values,
            @Nonnull T value) {
        return new Node<>(Optional.of(dimension), values, Optional.of(value));
    }

}

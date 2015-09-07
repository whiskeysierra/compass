package org.zalando.compass.core;

/*
 * ⁣​
 * Compass Core
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

import org.zalando.compass.api.Entry;

import javax.annotation.Nonnull;
import java.util.Map;

final class DefaultEntry<T> implements Entry<T> {

    private final Map<String, String> dimensions;
    private final T value;

    DefaultEntry(Map<String, String> dimensions, T value) {
        this.dimensions = dimensions;
        this.value = value;
    }

    @Override
    public Map<String, String> getDimensions() {
        return dimensions;
    }

    @Nonnull
    @Override
    public T getValue() {
        return value;
    }

}

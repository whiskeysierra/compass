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

import com.google.common.collect.Ordering;
import dagger.ObjectGraph;
import org.zalando.compass.api.Entry;
import org.zalando.compass.api.Key;
import org.zalando.compass.api.Values;
import org.zalando.compass.spi.Operator;
import org.zalando.compass.spi.Operators;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public final class Compass {

    private final Ordering<String> ordering;
    private final Operators operators;

    @Inject
    public Compass(final Ordering<String> ordering, final Operators operators) {
        this.ordering = ordering;
        this.operators = operators;
    }

    public <T> T get(final Key key, final Map<String, String> dimensions) {
        final Values values = null; // TODO these need to be sorted by prio+type rules already

        @SuppressWarnings("unchecked")
        final List<Entry<T>> entries = (List) values.get(key.getId());

        return entries.stream()
                .filter(entry -> matches(entry, dimensions))
                .findFirst()
                .map(Entry::getValue)
                .orElseThrow(IllegalStateException::new);
    }

    private <T, D> boolean matches(final Entry<T> entry, final Map<String, String> dimensions) {
        for (String dimension : ordering.sortedCopy(dimensions.keySet())) {
            if (entry.getDimensions().containsKey(dimension)) {
                final Operator<D> operator = operators.get(dimension);

                final D expected = operator.parse(dimensions.get(dimension));
                final D actual = operator.parse(entry.getDimensions().get(dimension));
                final int result = operator.compare(expected, actual);

                if (operator.test(result)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Values filter(final Values values, final Map<String, String> dimensions) {
        // TODO filter/reduce
        return values;
    }

    public static Compass create(final List<String> dimensions, final Operators operators) {
        final CompassModule module = new CompassModule(dimensions, operators);
        final ObjectGraph graph = ObjectGraph.create(module);
        return graph.get(Compass.class);
    }

}

package org.zalando.compass.spi;

/*
 * ⁣​
 * Compass SPI
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

import java.util.Map;
import java.util.ServiceLoader;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public interface Operators {

    <T> Operator<T> get(final String name) throws IllegalArgumentException;

    static Operators create() {
        final ServiceLoader<Operator> loader = load(Operator.class);
        final Map<String, Operator> operators = newArrayList(loader).stream()
                .collect(toMap(Operator::getName, identity()));

        return new ServiceLoaderOperators(operators);
    }

}

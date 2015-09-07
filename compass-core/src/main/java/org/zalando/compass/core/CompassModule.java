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
import dagger.Module;
import dagger.Provides;
import org.zalando.compass.spi.Operators;

import java.util.List;

@Module(injects = Compass.class)
final class CompassModule {

    private final List<String> dimensions;
    private final Operators operators;

    CompassModule(List<String> dimensions, Operators operators) {
        this.dimensions = dimensions;
        this.operators = operators;
    }

    @Provides
    List<String> dimensions() {
        return dimensions;
    }

    @Provides
    Ordering<String> ordering(final List<String> dimensions) {
        return Ordering.explicit(dimensions);
    }
    
    @Provides
    Operators operators() {
        return operators;
    }

}

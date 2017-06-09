package org.zalando.compass.resource;

import com.google.common.collect.ImmutableSet;

// TODO use the same logic to validate new dimensions
final class Keywords {

    static final ImmutableSet<String> RESERVED = ImmutableSet.of(
            // Zalando REST API Guidelines
            // http://zalando.github.io/restful-api-guidelines/naming/Naming.html#may-use-conventional-query-strings
            "q", // default query parameter
            "limit", // to restrict the number of entries
            "cursor", //key-based page start
            "offset", // numeric offset page start
            "sort", //comma-separated list of fields to sort
            "fields", //to retrieve a subset of fields
            "embed", // to expand embedded entFities

            // Google Cloud Platform: API Design Guide
            // https://cloud.google.com/apis/design/standard_fields
            "filter",
            "query",
            "page_token",
            "page_size",
            "order_by",
            "show_deleted",

            // Compass specific
            "key"
    );

}

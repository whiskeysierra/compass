package org.zalando.compass.core.infrastructure.http;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
final class MediaTypes {

    static final String JSON_MERGE_PATCH_VALUE = "application/merge-patch+json";
    static final String JSON_PATCH_VALUE = "application/json-patch+json";

    private MediaTypes() {

    }

}

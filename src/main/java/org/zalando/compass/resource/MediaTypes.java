package org.zalando.compass.resource;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.MediaType;

@VisibleForTesting
public final class MediaTypes {

    public static final String JSON_MERGE_PATCH_VALUE = "application/merge-patch+json";
    public static final MediaType JSON_MERGE_PATCH = MediaType.parseMediaType(JSON_MERGE_PATCH_VALUE);

    public static final String JSON_PATCH_VALUE = "application/json-patch+json";
    public static final MediaType JSON_PATCH = MediaType.parseMediaType(JSON_PATCH_VALUE);

}

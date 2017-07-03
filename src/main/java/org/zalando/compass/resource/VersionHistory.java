package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
final class VersionHistory<T> {

    Link next;
    List<T> revisions;

}

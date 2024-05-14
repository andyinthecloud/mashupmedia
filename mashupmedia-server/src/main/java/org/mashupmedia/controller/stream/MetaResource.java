package org.mashupmedia.controller.stream;

import org.mashupmedia.eums.MediaContentType;
import org.springframework.core.io.Resource;

import lombok.Builder;
import lombok.Getter;


@Builder(toBuilder = true)
@Getter
public class MetaResource {
    private final Resource resource;
    private final MediaContentType mediaContentType;

}

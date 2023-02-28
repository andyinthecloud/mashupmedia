package org.mashupmedia.controller.stream.resource;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.MediaItemHelper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MediaResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    private final MediaManager mediaManager;

    @Override
    @Nullable
    protected Resource getResource(HttpServletRequest request) throws IOException {
        final long mediaItemId = NumberUtils.toLong(request.getRequestURI().replaceFirst(".*\\/", ""));
        Assert.isTrue(mediaItemId > 0, "mediaItemId should be greater than zero");
        final MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        Assert.notNull(mediaItem, "media item should not be null");
        return new MediaResource(mediaItem);
    }

    @Override
    protected void setHeaders(HttpServletResponse response, Resource resource, @Nullable MediaType mediaType)
            throws IOException {
        super.setHeaders(response, resource, null);

        Assert.isInstanceOf(MediaResource.class, resource, "resource should be of type MediaResource");
        MediaResource mediaResource = (MediaResource) resource;
        response.setContentType(mediaResource.getMediaContentType().getMimeContentType());
    }

}

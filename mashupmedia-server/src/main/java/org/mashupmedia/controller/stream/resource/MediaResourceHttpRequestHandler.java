package org.mashupmedia.controller.stream.resource;

import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.component.TranscodeConfigurationComponent;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.service.MediaManager;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MediaResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    private final MediaManager mediaManager;
    private final TranscodeConfigurationComponent transcodeConfigurationComponent;

    @Override
    protected Resource getResource(@Nullable HttpServletRequest request) throws IOException {
        final long mediaItemId = NumberUtils.toLong(request.getRequestURI().replaceFirst(".*\\/", ""));
        Assert.isTrue(mediaItemId > 0, "mediaItemId should be greater than zero");
        final MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        Assert.notNull(mediaItem, "media item should not be null");

        if (mediaItem.getMashupMediaType() == MashupMediaType.MUSIC) {
            MediaResource mediaResource = mediaItem.getMediaResource(
                    transcodeConfigurationComponent.getTranscodeAudioMediaContentType());
            return new MediaFileSystemResource(mediaResource);
        }

        return null;
    }

    @Override
    protected void setHeaders(HttpServletResponse response, Resource resource, @Nullable MediaType mediaType)
            throws IOException {
        super.setHeaders(response, resource, null);

        Assert.isInstanceOf(MediaFileSystemResource.class, resource, "resource should be of type MediaResource");
        MediaFileSystemResource mediaResource = (MediaFileSystemResource) resource;
        response.setContentType(mediaResource.getMediaContentType().getMimeType());
    }

}

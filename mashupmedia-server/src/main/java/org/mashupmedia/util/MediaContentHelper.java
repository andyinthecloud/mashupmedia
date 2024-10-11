package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;

public class MediaContentHelper {


    // public static MediaEncoding createMediaEncoding(String fileName) {
    // String fileExtension = FileHelper.getFileExtension(fileName);
    // MediaEncoding mediaEncoding = new MediaEncoding();
    // mediaEncoding.setOriginal(true);
    // MediaContentType mediaContentType =
    // MediaItemHelper.getMediaContentType(fileExtension);
    // mediaEncoding.setMediaContentType(mediaContentType);
    // return mediaEncoding;
    // }

    public static MediaContentType getDefaultMediaContentType(MediaItem mediaItem) {
        if (mediaItem instanceof Track) {
            return MediaContentType.AUDIO_MP3;
        }

        return null;

    }

    public static boolean isCompatibleVideoFormat(MediaContentType mediaContentType) {
        if (mediaContentType == MediaContentType.VIDEO_MP4) {
            return true;
        }

        if (mediaContentType == MediaContentType.VIDEO_WEBM) {
            return true;
        }

        if (mediaContentType == MediaContentType.VIDEO_OGG) {
            return true;
        }

        return false;
    }

    public static boolean isCompatiblePhotoFormat(MediaContentType mediaContentType) {
        if (mediaContentType == MediaContentType.IMAGE_JPG) {
            return true;
        }

        if (mediaContentType == MediaContentType.IMAGE_GIF) {
            return true;
        }

        if (mediaContentType == MediaContentType.IMAGE_TIFF) {
            return true;
        }

        if (mediaContentType == MediaContentType.IMAGE_PNG) {
            return true;
        }

        return false;
    }
}

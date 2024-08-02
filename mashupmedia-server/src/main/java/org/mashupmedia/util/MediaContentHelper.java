package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;

public class MediaContentHelper {
    public static MediaContentType getMediaContentType(String format) {

        format = StringUtils.trimToEmpty(format).toLowerCase();

        if (format.endsWith("mpeg-1 layer 3") || format.endsWith("mp3")) {
            return MediaContentType.AUDIO_MP3;
        } else if (format.endsWith("webm")) {
            return MediaContentType.VIDEO_WEBM;
        } else if (format.endsWith("mp4") || format.endsWith("m4v")) {
            return MediaContentType.VIDEO_MP4;
        } else if (format.endsWith("ogv")) {
            return MediaContentType.VIDEO_OGG;
        } else if (format.endsWith("jpg") || format.endsWith("jpeg")) {
            return MediaContentType.IMAGE_JPG;
        } else if (format.endsWith("png")) {
            return MediaContentType.IMAGE_PNG;
        } else if (format.endsWith("gif")) {
            return MediaContentType.IMAGE_GIF;
        } else if (format.endsWith("tif") || format.endsWith("tiff")) {
            return MediaContentType.IMAGE_TIFF;
        }

        return MediaContentType.MEDIA_UNSUPPORTED;
    }

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

        return MediaContentType.MEDIA_UNSUPPORTED;

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

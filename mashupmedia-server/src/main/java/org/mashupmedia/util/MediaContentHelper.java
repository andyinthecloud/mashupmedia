package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;

public class MediaContentHelper {
    public static MediaContentType getMediaContentType(String format) {

        format = StringUtils.trimToEmpty(format);

        if (format.equalsIgnoreCase("MPEG-1 Layer 3") || format.equalsIgnoreCase("mp3")) {
            return MediaContentType.AUDIO_MP3;
        } else if (format.equalsIgnoreCase("webm")) {
            return MediaContentType.VIDEO_WEBM;
        } else if (format.equalsIgnoreCase("mp4") || format.equalsIgnoreCase("m4v")) {
            return MediaContentType.VIDEO_MP4;
        } else if (format.equalsIgnoreCase("ogv")) {
            return MediaContentType.VIDEO_OGG;
        } else if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
            return MediaContentType.IMAGE_JPG;
        } else if (format.equalsIgnoreCase("png")) {
            return MediaContentType.IMAGE_PNG;
        } else if (format.equalsIgnoreCase("gif")) {
            return MediaContentType.IMAGE_GIF;
        } else if (format.equalsIgnoreCase("tif") || format.equalsIgnoreCase("tiff")) {
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

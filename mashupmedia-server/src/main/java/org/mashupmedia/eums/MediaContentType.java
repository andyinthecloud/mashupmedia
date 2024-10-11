package org.mashupmedia.eums;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum MediaContentType {
    AUDIO_AAC(MashupMediaType.MUSIC, "aac"),
    AUDIO_MP3(MashupMediaType.MUSIC, "mpeg"),
    VIDEO_MP4(MashupMediaType.VIDEO, "mp4"),
    VIDEO_WEBM(MashupMediaType.VIDEO, "webm"),
    VIDEO_OGG(MashupMediaType.VIDEO, "ogg"),
    VIDEO_WMV(MashupMediaType.VIDEO, "x-ms-wmv"),
    IMAGE_JPG(MashupMediaType.PHOTO, "jpeg"),
    IMAGE_PNG(MashupMediaType.PHOTO, "ipng"),
    IMAGE_GIF(MashupMediaType.PHOTO, "gif"),
    IMAGE_TIFF(MashupMediaType.PHOTO, "tiff");

    private final MashupMediaType mashupMediaType;
    private final String contentType;

    private MediaContentType(MashupMediaType mashupMediaType, String mimeContentType) {
        this.mashupMediaType = mashupMediaType;
        this.contentType = mimeContentType;
    }

    public String getMimeType() {
        return mashupMediaType.getMimeGroup() + "/" + this.contentType;
    }

    public static MediaContentType getMediaContentType(String format) {

        format = StringUtils.trimToEmpty(format).toLowerCase();

        if (format.endsWith("mp3")) {
            return MediaContentType.AUDIO_MP3;
        } else if (format.endsWith("aac")) {
            return MediaContentType.AUDIO_AAC;
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

        return null;
    }

}

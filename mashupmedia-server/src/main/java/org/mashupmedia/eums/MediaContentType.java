package org.mashupmedia.eums;

public enum MediaContentType {
    AUDIO_MP3("audio/mpeg", 1),
    VIDEO_MP4("video/mp4", 1),
    VIDEO_WEBM("video/webm", 2),
    VIDEO_OGG("video/ogg", 3),
    VIDEO_WMV("video/x-ms-wmv", 4),
    IMAGE_JPG("image/jpeg", 1),
    IMAGE_PNG("image/png", 2),
    IMAGE_GIF("image/gif", 3),
    IMAGE_TIFF("image/tiff", 4),
    MEDIA_UNSUPPORTED("media/unsupported", 100);

    private final String contentType;
    private final int ranking;

    private MediaContentType(String mimeContentType, int ranking) {
        this.contentType = mimeContentType;
        this.ranking = ranking;
    }

    public String getContentType() {
        return contentType;
    }

    public int getRanking() {
        return ranking;
    }
	
}

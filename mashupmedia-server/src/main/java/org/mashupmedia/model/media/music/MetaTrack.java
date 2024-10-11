package org.mashupmedia.model.media.music;

import org.mashupmedia.util.GenreHelper;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class MetaTrack {
    private final int year;
    private final String title;
    private final int number;
    private final long length;
    private final long bitRate;
    private final String format;
    private final String artist;
    @Getter(AccessLevel.NONE)
    private final String album;
    private final String genreName;
    private final int discNumber;
    private final String extension;

    public Genre getGenre() {
        return GenreHelper.getGenre(genreName);
    }

    public String getAlbum() {
        if (getDiscNumber() > 1) {
            return this.album + " - Disk " + getDiscNumber();
        }

        return this.album;
    }
}

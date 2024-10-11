package org.mashupmedia.eums;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum MashupMediaType {
    MUSIC("audio"), 
    VIDEO("video"), 
    PHOTO("image");

    private final String mimeGroup;
    
    private MashupMediaType(String mimeGroup) {
        this.mimeGroup = mimeGroup;
    }

    public static MashupMediaType getMediaType(String value) {
        return Arrays.stream(values())
        .filter(t -> t.name().equalsIgnoreCase(value))
        .findAny().orElse(MUSIC);
    }
}

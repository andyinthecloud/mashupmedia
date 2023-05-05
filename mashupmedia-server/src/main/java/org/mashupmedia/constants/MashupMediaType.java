package org.mashupmedia.constants;

import java.util.Arrays;

public enum MashupMediaType {
    MUSIC, VIDEO, PHOTO;

    public static MashupMediaType getMediaType(String value) {
        return Arrays.stream(values())
        .filter(t -> t.name().equalsIgnoreCase(value))
        .findAny().orElse(MUSIC);
    }
}

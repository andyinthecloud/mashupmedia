package org.mashupmedia.service.media.audio;

import java.nio.file.Path;

import org.mashupmedia.model.media.music.MetaTrack;

public interface AudioMetaManager {
    MetaTrack getMetaTrack(Path path);
}

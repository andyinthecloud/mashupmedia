package org.mashupmedia.encode.command;

import java.util.List;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.model.media.MediaItem;

public interface EncodeCommands {
    String getEncoderPathKey();

    String getTestOutputParameter();

    List<String> getEncodingProcessCommands(String encoderPath, MediaItem mediaItem, MediaContentType mediaContentType)
            throws MediaItemEncodeException;
}

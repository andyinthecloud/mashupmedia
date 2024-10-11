package org.mashupmedia.encode.command;

import java.nio.file.Path;
import java.util.List;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MediaItemTranscodeException;

public interface EncodeCommands {
    String getEncoderPathKey();

    String getTestOutputParameter();

    List<String> getEncodingProcessCommands(String encoderPath, MediaContentType mediaContentType, Path inputPath, Path outputPath)
            throws MediaItemTranscodeException;
}

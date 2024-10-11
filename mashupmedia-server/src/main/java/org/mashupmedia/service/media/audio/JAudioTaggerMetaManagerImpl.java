package org.mashupmedia.service.media.audio;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.mashupmedia.model.media.music.MetaTrack;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JAudioTaggerMetaManagerImpl implements AudioMetaManager {

    @Override
    public MetaTrack getMetaTrack(Path path) {
        AudioFile audioFile;
        String fileTrackName = FilenameUtils.getBaseName(path.getFileName().toString());
        String fileExtension = FilenameUtils.getExtension(path.getFileName().toString());
        
        Path parentPath = path.getParent();
        String fileAlbumName = FilenameUtils.getBaseName(parentPath.getFileName().toString());

        Path grandParentPath = parentPath.getParent();
        String fileArtistName = FilenameUtils.getBaseName(grandParentPath.getFileName().toString());

        try {
            audioFile = AudioFileIO.read(path.toFile());
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            log.info("Unable to read meta tags in audio file", e);

            return MetaTrack.builder()
                    .title(fileTrackName)
                    .extension(fileExtension)
                    .album(fileAlbumName)
                    .artist(fileArtistName)
                    .build();
        }

        AudioHeader audioHeader = audioFile.getAudioHeader();
        Tag tag = audioFile.getTag();

        return MetaTrack.builder()
                .title(getField(tag, FieldKey.TITLE, fileTrackName))
                .extension(fileExtension)
                .year(NumberUtils.toInt(tag.getFirst(FieldKey.YEAR)))
                .number(NumberUtils.toInt(tag.getFirst(FieldKey.TRACK)))
                .length(audioHeader.getTrackLength())
                .genreName(getField(tag, FieldKey.GENRE))
                .artist(getField(tag, FieldKey.ALBUM_ARTIST, fileArtistName))
                .album(getField(tag, FieldKey.ALBUM, fileAlbumName))
                .discNumber(NumberUtils.toInt(tag.getFirst(FieldKey.DISC_NO)))
                .bitRate(audioHeader.getBitRateAsNumber())
                .format(audioHeader.getFormat())
                .build();
    }

    private String getField(Tag tag, FieldKey fieldKey) {
        return StringUtils.trimToEmpty(tag.getFirst(fieldKey));
    }

    private String getField(Tag tag, FieldKey fieldKey, String defaultValue) {
        String fieldName = getField(tag, fieldKey);
        if (StringUtils.isNotEmpty(fieldName)) {
            return fieldName;
        }

        return defaultValue;
    }



}

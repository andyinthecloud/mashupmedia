package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.TrackPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.util.TimeHelper;
import org.mashupmedia.util.TimeHelper.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class TrackMapper implements DomainMapper<Track, TrackPayload> {

    @Override
    public TrackPayload toDto(Track domain) {
        return TrackPayload.builder()
                .id(domain.getId())
                .name(domain.getTitle())
                .trackNumber(domain.getTrackNumber())
                .totalSeconds(domain.getTrackLength())
                .minutes(TimeHelper.getDurationUnit(domain.getTrackLength(),
                        TimeUnit.MINUTE))
                .seconds(TimeHelper.getDurationUnit(domain.getTrackLength(),
                        TimeUnit.SECOND))
                .encodedForWeb(domain.isEncodedForWeb())
                .build();
    }

    @Override
    public Track toDomain(TrackPayload payload) {
        return null;
    }

}

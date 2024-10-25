package org.mashupmedia.mapper.media.music;

import org.mashupmedia.component.TranscodeConfigurationComponent;
import org.mashupmedia.dto.media.music.TrackPayload;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.media.social.SocialConfiguration;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.TimeHelper;
import org.mashupmedia.util.TimeHelper.TimeUnit;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TrackMapper implements DomainMapper<Track, TrackPayload> {

        private final TranscodeConfigurationComponent transcodeConfigurationComponent;

        @Override
        public TrackPayload toPayload(Track domain) {
                MediaContentType audioMediaContentType = transcodeConfigurationComponent
                                .getTranscodeAudioMediaContentType();

                User user = AdminHelper.getLoggedInUser();
                SocialConfiguration socialConfiguration = domain.getSocialConfiguration();

                return TrackPayload.builder()
                                .id(domain.getId())
                                .name(domain.getTitle())
                                .trackNumber(domain.getTrackNumber())
                                .totalSeconds(domain.getTrackLength())
                                .minutes(TimeHelper.getDurationUnit(domain.getTrackLength(),
                                                TimeUnit.MINUTE))
                                .seconds(TimeHelper.getDurationUnit(domain.getTrackLength(),
                                                TimeUnit.SECOND))
                                .year(domain.getTrackYear())
                                .transcodedForWeb(domain.isTranscoded(audioMediaContentType))
                                .disableVotes(socialConfiguration.isDisableVotes())
                                .votedUp(socialConfiguration.isUserVotedUp(user))
                                .build();
        }

        @Override
        public Track toDomain(TrackPayload payload) {
                return null;
        }

}

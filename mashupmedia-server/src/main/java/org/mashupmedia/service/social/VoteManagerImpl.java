package org.mashupmedia.service.social;

import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.social.SocialConfiguration;
import org.mashupmedia.model.media.social.VoteType;
import org.mashupmedia.repository.social.SocialConfigurationRepository;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VoteManagerImpl implements VoteManager {
    
    private final MediaManager mediaManager;
    private final SocialConfigurationRepository socialConfigurationRepository;
    
    
    @Override
    public boolean isVotingDisabled(long mediaItemId) {
        MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        if (mediaItem == null) {
            return false;
        }

        SocialConfiguration socialConfiguration = mediaItem.getSocialConfiguration();
        return socialConfiguration.isDisableVotes();

    }

    @Override
    public void voteMediaItem(long mediaItemId) {
        MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        if (mediaItem == null) {
            log.error("Unable to find media item");
            return;
        }


        SocialConfiguration socialConfiguration = mediaItem.getSocialConfiguration();
        User user = AdminHelper.getLoggedInUser();

        boolean isVotedUp = socialConfiguration.isUserVotedUp(user);
        VoteType voteType = isVotedUp ? VoteType.DOWN_VOTE : VoteType.UP_VOTE;
        socialConfiguration.vote(user, voteType);
        socialConfigurationRepository.save(socialConfiguration);
    }

}

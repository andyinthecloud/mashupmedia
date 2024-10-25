package org.mashupmedia.service.social;

public interface VoteManager {
    boolean isVotingDisabled(long mediaItemId);
    void voteMediaItem(long mediaItemId);
}

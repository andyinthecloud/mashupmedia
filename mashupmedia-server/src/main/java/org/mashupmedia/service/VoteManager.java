package org.mashupmedia.service;

public interface VoteManager {

	public boolean voteLike(long mediaItemId, long userId);

	public boolean voteDislike(long mediaItemId, long userId);

	public void deleteVotesForMediaItem(long medaItemId);

}

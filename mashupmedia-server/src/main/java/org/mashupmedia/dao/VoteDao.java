package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.vote.DislikeVote;
import org.mashupmedia.model.vote.LikeVote;
import org.mashupmedia.model.vote.Vote;

public interface VoteDao {

	Vote getLatestVote(long userId, long mediaItemId);

	void saveDislikeVote(DislikeVote dislikeVote);

	void saveLikeVote(LikeVote likeVote);

	List<Vote> getVotesForMediaItem(long mediaItemId);

	void deleteVote(Vote vote);

}

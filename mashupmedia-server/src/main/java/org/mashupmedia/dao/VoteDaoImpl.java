package org.mashupmedia.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mashupmedia.model.vote.DislikeVote;
import org.mashupmedia.model.vote.LikeVote;
import org.mashupmedia.model.vote.Vote;
import org.springframework.stereotype.Repository;

@Repository
public class VoteDaoImpl extends BaseDaoImpl implements VoteDao {

	@Override
	public Vote getLatestVote(long userId, long mediaItemId) {
		TypedQuery<Vote> query = entityManager.createQuery(
				"from Vote where user.id = :userId and mediaItem.id = :mediaItemId "
						+ "and createdOn = (select max(tv.createdOn) from Vote tv where tv.user.id = :userId and tv.mediaItem.id = mediaItem.id)", Vote.class);
		query.setParameter("userId", userId);
		query.setParameter("mediaItemId", mediaItemId);
		Vote vote = getUniqueResult(query);
		return vote;

	}

	@Override
	public void saveDislikeVote(DislikeVote dislikeVote) {
		saveOrUpdate(dislikeVote);

	}

	@Override
	public void saveLikeVote(LikeVote likeVote) {
		saveOrUpdate(likeVote);
	}

	@Override
	public void deleteVote(Vote vote) {
		entityManager.remove(vote);
	}

	@Override
	public List<Vote> getVotesForMediaItem(long mediaItemId) {
		Query query = entityManager.createQuery("from Vote where mediaItem.id = :mediaItemId");
		query.setParameter("mediaItemId", mediaItemId);
		@SuppressWarnings("unchecked")
		List<Vote> votes = query.getResultList();
		return votes;
	}

}

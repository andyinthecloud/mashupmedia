package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.dao.VoteDao;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.vote.DislikeVote;
import org.mashupmedia.model.vote.LikeVote;
import org.mashupmedia.model.vote.Vote;
import org.mashupmedia.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class VoteManagerImpl implements VoteManager {
	
	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private VoteDao voteDao;

	@Override
	public boolean voteLike(long mediaItemId, long userId) {
		LikeVote likeVote = new LikeVote();
		likeVote = (LikeVote) prepareVote(mediaItemId, userId, likeVote);
		if (likeVote == null) {
			return false;
		}

		saveLikeVote(likeVote);
		saveMediaItemVote(likeVote.getMediaItem(), 1);
		return true;
	}

	private void saveMediaItemVote(MediaItem mediaItem, int i) {
		int vote = mediaItem.getVote();
		vote = vote + i;
		mediaItem.setVote(vote);
		mediaManager.updateMediaItem(mediaItem);
	}

	@Override
	public boolean voteDislike(long mediaItemId, long userId) {
		DislikeVote dislikeVote = new DislikeVote();
		dislikeVote = (DislikeVote) prepareVote(mediaItemId, userId, dislikeVote);
		if (dislikeVote == null) {
			return false;
		}

		saveDislikeVote(dislikeVote);
		saveMediaItemVote(dislikeVote.getMediaItem(), -1);
		return true;
	}

	private void saveDislikeVote(DislikeVote dislikeVote) {
		voteDao.saveDislikeVote(dislikeVote);
	}

	private void saveLikeVote(LikeVote likeVote) {
		voteDao.saveLikeVote(likeVote);
	}

	protected Vote prepareVote(long mediaItemId, long userId, Vote vote) {
		Date date = new Date();
		Date latestVoteDate = date;

		Vote latestVote = getLatestVote(userId, mediaItemId);
		if (latestVote != null) {
			latestVoteDate = latestVote.getCreatedOn();
		}

		long differenceInSeconds = DateHelper.getDifferenceInSeconds(date, latestVoteDate);
		long voteUserWaitSeconds = NumberUtils.toLong(configurationManager.getConfigurationValue(MashUpMediaConstants.VOTE_USER_WAIT_SECONDS));

		if (differenceInSeconds < voteUserWaitSeconds) {
			return null;
		}

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		User user = adminManager.getUser(userId);
		vote.setCreatedOn(date);
		vote.setMediaItem(mediaItem);
		vote.setUser(user);
		return vote;

	}

	private Vote getLatestVote(long userId, Long mediaItemId) {
		Vote vote = voteDao.getLatestVote(userId, mediaItemId);
		return vote;
	}
	
	@Override
	public void deleteVotesForMediaItem(long mediaItemId) {
		List<Vote> votes = getVotesForMediaItem(mediaItemId);
		if (votes == null || votes.isEmpty()) {
			return;
		}

		log.info("Deleting " + votes.size() + " votes...");
		for (Vote vote : votes) {
			voteDao.deleteVote(vote);
		}
		
	}

	private List<Vote> getVotesForMediaItem(long mediaItemId) {
		List<Vote> votes = voteDao.getVotesForMediaItem(mediaItemId);
		return votes;
	}

}

package org.mashupmedia.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.media.Video;
import org.mashupmedia.model.media.VideoResolution;
import org.mashupmedia.util.DaoHelper;
import org.springframework.stereotype.Repository;

@Repository
public class VideoDaoImpl extends BaseDaoImpl implements VideoDao {

	@Override
	public List<VideoResolution> getVideoResolutions() {
		Query query = sessionFactory.getCurrentSession().createQuery("from VideoResolution order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<VideoResolution> videoResolutions = query.list();
		return videoResolutions;
	}

	@Override
	public void saveVideoResolution(VideoResolution videoResolution) {
		saveOrUpdate(videoResolution);
	}

	@Override
	public VideoResolution getVideoResolution(long videoResolutionId) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from VideoResolution where id = :videoResolutionId");
		query.setLong("videoResolutionId", videoResolutionId);
		query.setCacheable(true);
		VideoResolution videoResolution = (VideoResolution) query.uniqueResult();
		return videoResolution;
	}

	@Override
	public void saveVideo(Video video, boolean isSessionFlush) {
		saveOrUpdate(video);
		flushSession(isSessionFlush);

	}

	@Override
	public void saveVideo(Video video) {
		saveVideo(video, false);
	}

	@Override
	public List<Video> getVideos(Collection<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder("select v from Video v a join v.library.groups g");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by v.name");
		Query query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString());
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Video> videos = query.list();
		return videos;
	}

}

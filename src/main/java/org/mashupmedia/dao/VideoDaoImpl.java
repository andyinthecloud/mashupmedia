package org.mashupmedia.dao;

import java.util.Collection;
import java.util.Date;
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
		Query query = sessionFactory.getCurrentSession().createQuery("from VideoResolution order by width");
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
	public VideoResolution getVideoResolution(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from VideoResolution where name = :name");
		query.setString("name", name);
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
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct v from Video v join v.library.groups g where v.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by v.displayTitle");
		Query query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString());
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Video> videos = query.list();
		return videos;
	}

	@Override
	public int removeObsoleteVideos(long libraryId, Date date) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete Video v where v.updatedOn < :date and v.library.id = :libraryId");
		query.setDate("date", date);
		query.setLong("libraryId", libraryId);
		int totalDeletedVideos = query.executeUpdate();
		return totalDeletedVideos;
	}

	@Override
	public List<Video> getObsoleteVideos(long libraryId, Date date) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Video v where v.updatedOn < :date and v.library.id = :libraryId");
		query.setDate("date", date);
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Video> videos = query.list();
		return videos;
	}

	@Override
	public Video getVideoByPath(String path) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Video v where v.path = :path");
		query.setString("path", path);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Video> videos = query.list();

		if (videos == null || videos.isEmpty()) {
			return null;
		}

		if (videos.size() > 1) {
			logger.error("Duplicate videos found for the same file. Attempting to remove files");
			Video video = videos.get(0);
			videos.remove(video);
			deleteVideos(videos);
			return video;
		}

		return videos.get(0);
	}
	
	@Override
	public Video getVideo(long videoId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Video v where v.id = :videoId");
		query.setLong("videoId", videoId);
		query.setCacheable(true);
		
		@SuppressWarnings("unchecked")
		List<Video> videos = query.list();
		if (videos == null || videos.isEmpty()) {
			return null;
		}
		
		return videos.get(0);
		
	}
	
	@Override
	public int getTotalVideosWithSameName(String title) {
		Query query = sessionFactory.getCurrentSession().createQuery("select count(*) from Video v where v.searchText = :title");
		query.setString("title", title);
		query.setCacheable(true);
		Long count = (Long) query.uniqueResult();
		return count.intValue();
	}

	protected void deleteVideos(List<Video> videos) {
		if (videos == null || videos.isEmpty()) {
			return;
		}
		for (Video video : videos) {
			logger.info("Deleting video: " + video.getPath());
			sessionFactory.getCurrentSession().delete(video);
		}

	}

}

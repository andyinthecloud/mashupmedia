package org.mashupmedia.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.util.DaoHelper;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class VideoDaoImpl extends BaseDaoImpl implements VideoDao {

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
	public List<Video> getVideos(Long userId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct v from Video v  ");
		queryBuilder.append(" join v.library l");
		queryBuilder.append(" left join l.users u");
		queryBuilder.append(" where v.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
		queryBuilder.append(" order by v.displayTitle");
		Query query = entityManager.createQuery(queryBuilder.toString());
		@SuppressWarnings("unchecked")
		List<Video> videos = query.getResultList();
		return videos;
	}

	@Override
	public int removeObsoleteVideos(long libraryId, Date date) {
		Query query = entityManager.createQuery(
				"delete Video v where v.updatedOn < :date and v.library.id = :libraryId");
		query.setParameter("date", date);
		query.setParameter("libraryId", libraryId);
		int totalDeletedVideos = query.executeUpdate();
		return totalDeletedVideos;
	}

	@Override
	public List<Video> getObsoleteVideos(long libraryId, Date date) {
		Query query = entityManager.createQuery(
				"from Video v where v.updatedOn < :date and v.library.id = :libraryId");
		query.setParameter("date", date);
		query.setParameter("libraryId", libraryId);
		@SuppressWarnings("unchecked")
		List<Video> videos = query.getResultList();
		return videos;
	}

	@Override
	public Video getVideoByPath(String path) {
		Query query = entityManager.createQuery("from Video v where v.path = :path");
		query.setParameter("path", path);
		@SuppressWarnings("unchecked")
		List<Video> videos = query.getResultList();

		if (videos == null || videos.isEmpty()) {
			return null;
		}

		if (videos.size() > 1) {
			log.error("Duplicate videos found for the same file. Attempting to remove files");
			Video video = videos.get(0);
			videos.remove(video);
			deleteVideos(videos);
			return video;
		}

		return videos.get(0);
	}

	@Override
	public Video getVideo(long videoId) {
		Query query = entityManager.createQuery("from Video v where v.id = :videoId");
		query.setParameter("videoId", videoId);

		@SuppressWarnings("unchecked")
		List<Video> videos = query.getResultList();
		if (videos == null || videos.isEmpty()) {
			return null;
		}

		return videos.get(0);

	}

	@Override
	public int getTotalVideosWithSameName(String title) {
		TypedQuery<Long> query = entityManager.createQuery(
				"select count(*) from Video v where v.searchText = :title", Long.class);
		query.setParameter("title", title);
		Long count = getUniqueResult(query);
		return count.intValue();
	}

	protected void deleteVideos(List<Video> videos) {
		if (videos == null || videos.isEmpty()) {
			return;
		}
		for (Video video : videos) {
			log.info("Deleting video: " + video.getPath());
			entityManager.remove(video);
		}

	}

}

package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.media.VideoResolution;
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

}

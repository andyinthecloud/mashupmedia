package org.mashupmedia.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.music.TrackRepository;
import org.mashupmedia.repository.media.music.TrackSpecifications;
import org.mashupmedia.util.AdminHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MediaManagerImpl implements MediaManager {

	private final MediaDao mediaDao;
	private final TrackRepository trackRepository;
	private final MashupMediaSecurityManager mashupMediaSecurityManager;

	@Override
	public List<MediaItem> getMediaItemsForLibrary(long libraryId) {
		List<MediaItem> mediaList = mediaDao.getMediaItems(libraryId);
		return mediaList;
	}

	@Override
	public MediaItem getMediaItem(long mediaItemId) {
		MediaItem mediaItem = mediaDao.getMediaItem(mediaItemId);
		return mediaItem;
	}

	@Override
	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages) {
		mediaDao.deleteAlbumArtImages(albumArtImages);

	}

	@Override
	public List<AlbumArtImage> getAlbumArtImages(long libraryId) {
		List<AlbumArtImage> albumArtImages = mediaDao.getAlbumArtImages(libraryId);
		return albumArtImages;
	}

	@Override
	public void updateMediaItem(MediaItem mediaItem) {
		mediaItem.setUpdatedOn(new Date());
		mediaDao.updateMediaItem(mediaItem);
	}

	private Specification<Track> getFindSpecification(MediaItemSearchCriteria mediaItemSearchCriteria) {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		String text = mediaItemSearchCriteria.getSearchText();

		Specification<Track> specification = TrackSpecifications.hasUser(loggedInUserId)
				.and(TrackSpecifications.hasTitleLike(text)
						.or(TrackSpecifications.hasAlbumNameLike(text))
						.or(TrackSpecifications.hasArtistNameLike(text)))
				.and(TrackSpecifications.hasGenre(mediaItemSearchCriteria.getGenreIdNames()));

		for (Integer decade : mediaItemSearchCriteria.getDecades()) {
			specification = specification.and(TrackSpecifications.hasDecade(decade));
		}

		return specification;
	}


	@Override
	public Page<Track> findMusicTracks(MediaItemSearchCriteria mediaItemSearchCriteria, Pageable pageable) {
		Specification<Track> specification = getFindSpecification(mediaItemSearchCriteria);
		Slice<Track> slice = trackRepository.findAll(specification, pageable);
		return new PageImpl<>(slice.getContent(), pageable, 0);
	}

	@Override
	public long countMusicTracks(MediaItemSearchCriteria mediaItemSearchCriteria) {
		Specification<Track> specification = getFindSpecification(mediaItemSearchCriteria);
		return trackRepository.count(specification);
	}

	@Override
	public void saveMediaItem(MediaItem mediaItem) {
		mediaItem.setUpdatedOn(new Date());
		mediaDao.saveMediaItem(mediaItem);
	}

}

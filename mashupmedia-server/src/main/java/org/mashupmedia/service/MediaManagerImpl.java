package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.SearchMediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.repository.media.music.MusicAlbumRepository;
import org.mashupmedia.repository.media.music.TrackRepository;
import org.mashupmedia.repository.media.music.TrackSpecifications;
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
	private final MusicAlbumRepository musicAlbumRepository;
	private final ArtistRepository artistRepository;
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

	@Override
	public List<SearchMediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria) {

		List<SearchMediaItem> searchMediaItems = new ArrayList<>();
		String text = mediaItemSearchCriteria.getSearchText();

		List<Artist> artists = artistRepository.findByNameContainingIgnoreCaseOrderByName(text);
		searchMediaItems.addAll(artists.stream()
				.map(artist -> SearchMediaItem.builder()
						.result(artist)
						.build())
				.collect(Collectors.toList()));

		List<Album> albums = musicAlbumRepository.findByNameContainingIgnoreCaseOrderByName(text);
		searchMediaItems.addAll(albums.stream()
				.map(album -> SearchMediaItem.builder()
						.result(album)
						.build())
				.collect(Collectors.toList()));

		List<Long> userGroupIds = mashupMediaSecurityManager.getLoggedInUserGroupIds();
		
		Specification<Track> specification = TrackSpecifications.hasGroup(userGroupIds)
				.and(TrackSpecifications.hasTitleLike(text))
				.and(TrackSpecifications.hasGenre(mediaItemSearchCriteria.getGenreIdNames()));

		for(Integer decade: mediaItemSearchCriteria.getDecades()) {
			specification = specification.and(TrackSpecifications.hasDecade(decade));
		}

		List<Track> tracks = trackRepository.findAll(specification);

		searchMediaItems.addAll(tracks.stream()
				.map(track -> SearchMediaItem.builder()
						.result(track)
						.build())
				.collect(Collectors.toList()));

		return searchMediaItems;
	}

	@Override
	public void saveMediaItem(MediaItem mediaItem) {
		mediaItem.setUpdatedOn(new Date());
		mediaDao.saveMediaItem(mediaItem);
	}

}

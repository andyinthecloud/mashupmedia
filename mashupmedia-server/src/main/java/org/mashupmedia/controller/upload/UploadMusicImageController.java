package org.mashupmedia.controller.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.MetaEntityPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ErrorPayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.exception.UserStorageException;
import org.mashupmedia.mapper.media.MetaImageMapper;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.MusicResourceManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/upload/music")
@RequiredArgsConstructor
public class UploadMusicImageController {

	private final MusicResourceManager musicResourceManager;
	private final MusicManager musicManager;
	private final MetaImageMapper metaImageMapper;

	@PostMapping(value = "/artist/images", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServerResponsePayload<List<MetaEntityPayload>>> postArtistImages(
			@RequestParam("artistId") long artistId,
			@RequestParam("files") MultipartFile[] files) {

		Artist artist = musicManager.getArtist(artistId);
		AdminHelper.checkAccess(artist.getUser());

		List<MetaImage> metaImages = new ArrayList<>();
		for (MultipartFile file : files) {
			try {
				metaImages.add(musicResourceManager.storeArtistImage(artistId, file));
			} catch (UserStorageException e) {
				return getOutOfSpaceErrorPayload();
			}
		}

		return getMetaImagesPayload(metaImages);
	}

	@PostMapping(value = "/artist/tracks", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServerResponsePayload<Boolean>> postArtistTracks(
			@RequestParam("libraryId") long libraryId,
			@RequestParam("albumId") long albumId,
			@RequestParam("decade") Integer decade,
			@RequestParam("genreIdName") String genreIdName,
			@RequestParam("files") MultipartFile[] files) {

		Album album = musicManager.getAlbum(albumId);
		Artist artist = album.getArtist();
		AdminHelper.checkAccess(artist.getUser());

		for (MultipartFile file: files) {
			try {
				musicResourceManager.storeTrack(libraryId, albumId, decade, genreIdName, file);
			} catch (UserStorageException e) {
				return getOutOfSpaceErrorPayload();
			}
		}

		return ResponseEntity
				.ok()
				.body(
						ServerResponsePayload.<Boolean>builder()
								.payload(Boolean.TRUE)
								.build());
	}


	private <T> ResponseEntity<ServerResponsePayload<T>> getOutOfSpaceErrorPayload() {
		return ResponseEntity
				.badRequest()
				.body(
						ServerResponsePayload.<T>builder()
								.errorPayload(ErrorPayload.builder()
										.errorCode(ErrorCode.OUT_OF_STORAGE.getErrorCode())
										.build())
								.build());
	}

	private ResponseEntity<ServerResponsePayload<List<MetaEntityPayload>>> getMetaImagesPayload(
			List<MetaImage> metaImages) {
		return ResponseEntity
				.ok()
				.body(
						ServerResponsePayload.<List<MetaEntityPayload>>builder()
								.payload(
										metaImages
												.stream()
												.map(metaImageMapper::toPayload)
												.collect(Collectors.toList()))
								.build());

	}

	@PostMapping(value = "/album/images", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServerResponsePayload<List<MetaEntityPayload>>> postAlbumImages(
			@RequestParam("albumId") long albumId,
			@RequestParam("files") MultipartFile[] files) {

		User user = AdminHelper.getLoggedInUser();
		Album album = musicManager.getAlbum(albumId);
		Artist artist = album.getArtist();
		if (!user.equals(artist.getUser())) {
			throw new SecurityException("User cannot modify this album");
		}

		List<MetaImage> metaImages = new ArrayList<>();
		for (MultipartFile file : files) {
			try {
				metaImages.add(musicResourceManager.storeAlbumImage(albumId, file));
			} catch (UserStorageException e) {
				return getOutOfSpaceErrorPayload();
			}
		}

		return getMetaImagesPayload(metaImages);
	}

}

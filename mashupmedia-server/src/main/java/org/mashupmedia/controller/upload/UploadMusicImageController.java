package org.mashupmedia.controller.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.MetaEntityPayload;
import org.mashupmedia.mapper.media.MetaImageMapper;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MetaImage;
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
	public ResponseEntity<List<MetaEntityPayload>> handleFileUpload(
			@RequestParam("artistId") long artistId,
			@RequestParam("files") MultipartFile[] files) {

		User user = AdminHelper.getLoggedInUser();
		Artist artist = musicManager.getArtist(artistId);
		if (!user.equals(artist.getUser())) {
			throw new SecurityException("User cannot modify this artist");
		}

		List<MetaImage> metaImages = new ArrayList<>();
		for (MultipartFile file : files) {
			metaImages.add(musicResourceManager.storeArtistImage(artistId, file));
		}

		return ResponseEntity
				.ok()
				.body(metaImages
						.stream()
						.map(metaImageMapper::toPayload)
						.collect(Collectors.toList()));
	}

}

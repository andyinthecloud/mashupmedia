package org.mashupmedia.controller.upload;

import java.util.List;

import org.mashupmedia.dto.media.MetaImagePayload;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.MusicResourceManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.MetaEntityHelper;
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

	@PostMapping(value = "/artist/images", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MetaImagePayload> handleFileUpload(
			@RequestParam("artistId") long artistId,
			@RequestParam("files") MultipartFile[] files) {

		User user = AdminHelper.getLoggedInUser();
		Artist artist = musicManager.getArtist(artistId);
		if (!user.equals(artist.getUser())) {
			throw new SecurityException("User cannot modify this artist");
		}

		for (MultipartFile file : files) {
			musicResourceManager.storeArtistImage(artistId, file);
		}

		MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
		List<MetaImage> metaImages = metaImageHelper.getSortedEntities(artist.getMetaImages());

		MetaImagePayload metaImagePayload = MetaImagePayload
				.builder()
				.ranks(metaImages.stream()
						.map(MetaImage::getRank)
						.toArray(Integer[]::new))
				.build();

		return ResponseEntity
				.ok()
				.body(metaImagePayload);
	}

}

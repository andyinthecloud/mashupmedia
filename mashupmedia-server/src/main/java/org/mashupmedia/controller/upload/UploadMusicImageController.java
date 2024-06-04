package org.mashupmedia.controller.upload;

import org.mashupmedia.model.account.User;
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

	@PostMapping(value = "/artist/images", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> handleFileUpload(
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
		
		return ResponseEntity
				.ok()
				.body(true);
	}

}

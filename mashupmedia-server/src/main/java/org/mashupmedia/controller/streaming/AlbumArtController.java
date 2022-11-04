package org.mashupmedia.controller.streaming;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.hibernate.type.ImageType;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/streaming/album-art")
@RequiredArgsConstructor
public class AlbumArtController {

    private final MusicManager musicManager;

    private final MashupMediaSecurityManager securityManager;

    @GetMapping(value = "/{albumId}")
    public ResponseEntity<InputStreamResource> getAlbumArt(@PathVariable long albumId,
            @RequestParam String streamingToken,
            @RequestParam(value = "imageType", required = false) ImageType imageType) throws IOException {

        if (!securityManager.isStreamingTokenValid(streamingToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Album album = musicManager.getAlbum(albumId);
        AlbumArtImage albumArtImage = album.getAlbumArtImage();

        String imagePath = getImagePath(imageType, albumArtImage);
        InputStream inputStream = new FileInputStream(imagePath);

        // MediaType mediaType = MediaItemHelper.getMediaType(albumArtImage.getContentType());

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(inputStream));

    }

    private String getImagePath(ImageType imageType, AlbumArtImage albumArtImage) {
        if (imageType == null) {
            return albumArtImage.getThumbnailUrl();
        }

        return albumArtImage.getUrl();
    }

}

package org.mashupmedia.controller.streaming;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.hibernate.type.ImageType;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.SecurityManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/streaming/album-art")
@RequiredArgsConstructor
public class AlbumArtController {
    
    private final MusicManager musicManager;

    private final SecurityManager securityManager;

    @GetMapping(value = "/{streamingToken}/{albumId}")
    public byte[]  getAlbumArt(@PathVariable String streamingToken, @PathVariable long albumId,
             @RequestParam(value = "imageType", required = false) ImageType imageType) throws IOException {

        if (!securityManager.isStreamingTokenValid(streamingToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Album album = musicManager.getAlbum(albumId);
        AlbumArtImage albumArtImage = album.getAlbumArtImage();

        String imagePath = getImagePath(imageType, albumArtImage);
        InputStream inputStream = new FileInputStream(imagePath);
        return IOUtils.toByteArray(inputStream);
    }

    private String getImagePath(ImageType imageType, AlbumArtImage albumArtImage) {
        if (imageType == null) {
            return albumArtImage.getThumbnailUrl();
        }

        return albumArtImage.getUrl();
    }

}

package org.mashupmedia.controller.rest.media.music;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/music/albums")
public class AlbumController {

    private final MusicManager musicManager;

    @GetMapping(value = "/album-art/{albumId}")
    public byte[]  getAlbumArt(@PathVariable long albumId,
             @RequestParam(value = "imageType", required = false) ImageType imageType) throws IOException {

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

package org.mashupmedia.controller.stream;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/stream/secure/music/album-art")
@RequiredArgsConstructor
public class AlbumArtController {

    private static final String DEFAULT_MUSIC_ALBUM_ART = "/images/default-music-album-art.png";
    private static final MediaContentType DEFAULT_MUSIC_ALBUM_ART_CONTENT_TYPE = MediaContentType.IMAGE_PNG;

    private final MusicManager musicManager;

    private final MashupMediaSecurityManager securityManager;

    @GetMapping(value = "/{albumId}")
    public @ResponseBody Resource getAlbumArt(@PathVariable long albumId,
            @RequestParam String mediaToken,
            @RequestParam(value = "imageType", required = false) ImageType imageType,
            final HttpServletResponse httpServletResponse) throws IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Album album = musicManager.getAlbum(albumId);
        AlbumArtImage albumArtImage = album.getAlbumArtImage();

        MediaContentType mediaContentType = albumArtImage == null
                ? DEFAULT_MUSIC_ALBUM_ART_CONTENT_TYPE
                : MediaItemHelper.getMediaContentType(albumArtImage.getContentType());

        httpServletResponse.setContentType(mediaContentType.getContentType());

        String imagePath = getImagePath(imageType, albumArtImage);
        File albumArtFile = StringUtils.isEmpty(imagePath)
                ? null
                : new File(imagePath);

        Resource resource = null;

        if (albumArtFile != null && albumArtFile.isFile()) {
            resource = new FileSystemResource(albumArtFile);
        } else {
            resource = new ClassPathResource(DEFAULT_MUSIC_ALBUM_ART);
        }

        return resource;
    }

    private String getImagePath(ImageType imageType, AlbumArtImage albumArtImage) {
        if (albumArtImage == null) {
            return null;
        }

        if (imageType == ImageType.THUMBNAIL) {
            return albumArtImage.getThumbnailUrl();
        } else {
            return albumArtImage.getUrl();
        }
    }

}

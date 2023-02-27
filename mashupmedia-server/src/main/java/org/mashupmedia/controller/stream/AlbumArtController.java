package org.mashupmedia.controller.stream;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/stream/secure/music/album-art")
@RequiredArgsConstructor
public class AlbumArtController {

    public static final String IMAGE_PATH_DEFAULT_ALBUM_ART = "/images/default-album-art.png";

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

        MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(albumArtImage.getContentType());
        httpServletResponse.setContentType(mediaContentType.getMimeContentType());

        String imagePath = getImagePath(imageType, albumArtImage);

        FileSystemResource fileSystemResource = null;
        File albumArtFile = new File(imagePath);

        if (albumArtFile.isFile()) {
            fileSystemResource = new FileSystemResource(albumArtFile);
        } else {
            fileSystemResource = new FileSystemResource(IMAGE_PATH_DEFAULT_ALBUM_ART);
        }

        return fileSystemResource;
    }

    private String getImagePath(ImageType imageType, AlbumArtImage albumArtImage) {
        if (imageType == ImageType.THUMBNAIL) {
            return albumArtImage.getThumbnailUrl();
        } else {
            return albumArtImage.getUrl();
        }
    }

}

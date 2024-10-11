package org.mashupmedia.controller.stream;

import java.io.IOException;

import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/stream/secure/music/artist-art")
@RequiredArgsConstructor
public class ArtistMetaImageController extends MetaImageController{

    private final MusicManager musicManager;

    private final MashupMediaSecurityManager securityManager;

    @Override
    protected String getDefaultImagePath() {
        return "/images/default-music-artist.jpg";
    }

    @GetMapping(value = "/{artistId}")
    public @ResponseBody Resource getAlbumArt(@PathVariable long artistId,
            @RequestParam String mediaToken,
            @RequestParam(value = "imageType", required = false) ImageType imageType,
            @RequestParam(value = "id", required = false) Integer imageId,
            final HttpServletResponse httpServletResponse) throws IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Artist artist = musicManager.getArtist(artistId);
        Assert.notNull(artist, "Expecting an artist");

        MetaResource metaResource = getHighlightedMetaImage(artist.getMetaImages(), imageType, imageId);
        httpServletResponse.setContentType(metaResource.getMediaContentType().getMimeType());
        return metaResource.getResource();
    }

}

package org.mashupmedia.controller.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mashupmedia.comparator.MetaEntityComparator;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
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
@RequestMapping("/stream/secure/music/album-art")
@RequiredArgsConstructor
public class AlbumMetaImageController extends MetaImageController {

    private final MusicManager musicManager;

    private final MashupMediaSecurityManager securityManager;

    @Override
    protected String getDefaultImagePath() {
        return "/images/default-music-album.jpg";
    }

    @GetMapping(value = "/{albumId}")
    public @ResponseBody Resource getAlbumArt(@PathVariable long albumId,
            @RequestParam String mediaToken,
            @RequestParam(value = "imageType", required = false) ImageType imageType,
            @RequestParam(value = "index", required = false) Integer index,
            final HttpServletResponse httpServletResponse) throws IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Album album = musicManager.getAlbum(albumId);
        Assert.notNull(album, "Expecting an album");

        List<MetaImage> metaImages = new ArrayList<>(album.getMetaImages());
        Collections.sort(metaImages, new MetaEntityComparator());



        MetaResource metaResource = getHighlightedMetaImage(album.getMetaImages(), imageType, index);
        httpServletResponse.setContentType(metaResource.getMediaContentType().getContentType());
        return metaResource.getResource();
    }

}

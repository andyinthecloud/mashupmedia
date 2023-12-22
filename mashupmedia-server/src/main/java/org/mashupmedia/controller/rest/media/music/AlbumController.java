package org.mashupmedia.controller.rest.media.music;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.AlbumWithArtistPayload;
import org.mashupmedia.dto.media.music.AlbumWithTracksAndArtistPayload;
import org.mashupmedia.mapper.media.music.AlbumWithArtistMapper;
import org.mashupmedia.mapper.media.music.AlbumWithTracksMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private/music/albums")
public class AlbumController {
    private final static int MAX_RANDOM_ALBUMS = 20;

    private final MusicManager musicManager;

    private final MashupMediaSecurityManager mashupMediaSecurityManager;

    private final AlbumWithArtistMapper albumWithArtistMapper;

    private final AlbumWithTracksMapper albumWithTracksMapper;

    private enum SortAlbum {
        RANDOM
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecureMediaPayload<AlbumWithArtistPayload>> geAlbums(
            @RequestParam(name = "sort", required = false) SortAlbum sortAlbum) {
        User user = AdminHelper.getLoggedInUser();
        String streamingToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());

        List<SecureMediaPayload<AlbumWithArtistPayload>> secureAlbums = new ArrayList<>();
        if (sortAlbum == null || sortAlbum == SortAlbum.RANDOM) {
            secureAlbums = musicManager.getRandomAlbums(MAX_RANDOM_ALBUMS)
                    .stream()
                    .map(album -> albumWithArtistMapper.toDto(album, streamingToken))
                    .collect(Collectors.toList());
        }

        return secureAlbums;
    }

    @GetMapping(value = "/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SecureMediaPayload<AlbumWithTracksAndArtistPayload> getArtist(@PathVariable long albumId) {
        User user = AdminHelper.getLoggedInUser();
        String streamingToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());
        Album album = musicManager.getAlbum(albumId);
        return albumWithTracksMapper.toDto(album, streamingToken);
    }

}

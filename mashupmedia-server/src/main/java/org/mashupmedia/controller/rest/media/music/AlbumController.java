package org.mashupmedia.controller.rest.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.AlbumWithArtistPayload;
import org.mashupmedia.mapper.media.music.AlbumWithArtistMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/music/albums")
public class AlbumController {
    private final static int MAX_RANDOM_ALBUMS = 20;

    private final MusicManager musicManager;

    private final MashupMediaSecurityManager mashupMediaSecurityManager;

    private final AlbumWithArtistMapper albumWithArtistMapper;

    @GetMapping(value = "/random", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecureMediaPayload<AlbumWithArtistPayload>> getRandomAlbums() {
        User user = AdminHelper.getLoggedInUser();
        String streamingToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());

        return musicManager.getRandomAlbums(MAX_RANDOM_ALBUMS)
                .stream()
                .map(album -> albumWithArtistMapper.toDto(album, streamingToken))
                .collect(Collectors.toList());
    }

}

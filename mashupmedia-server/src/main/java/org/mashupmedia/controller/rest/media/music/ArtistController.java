package org.mashupmedia.controller.rest.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureTokenPayload;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.dto.media.music.ArtistWithAlbumsPayload;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.mapper.media.music.ArtistWithAlbumsMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/music/artists")
public class ArtistController {

    private final MusicManager musicManager;
    private final ArtistMapper artistMapper;
    private final ArtistWithAlbumsMapper artistWithAlbumsMapper;
    private final MashupMediaSecurityManager mashupMediaSecurityManager;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArtistPayload> getArtists() {
        return musicManager.getArtists()
                .stream()
                .map(artistMapper::toDto)
                .collect(Collectors.toList());

    }

    @GetMapping(value = "/{artistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SecureTokenPayload<ArtistWithAlbumsPayload> getArtist(@PathVariable long artistId) {
        User user = AdminHelper.getLoggedInUser();
        String streamingToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());
        var t =  artistWithAlbumsMapper.toDto(musicManager.getArtist(artistId), streamingToken);
        return t;

    }

}

package org.mashupmedia.controller.rest.authenticated.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.dto.media.music.ArtistWithAlbumsPayload;
import org.mashupmedia.dto.media.music.CreateArtistPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.JsonNameType;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.exception.ContainsMediaItemsException;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.mapper.media.music.ArtistWithAlbumsMapper;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private/music/artists")
public class ArtistController {

    private final MusicManager musicManager;
    private final ArtistMapper artistMapper;
    private final ArtistWithAlbumsMapper artistWithAlbumsMapper;
    private final MashupMediaSecurityManager mashupMediaSecurityManager;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArtistPayload> getArtists() {
        return musicManager.getArtists()
                .stream()
                .map(artistMapper::toPayloadList)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{artistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SecureMediaPayload<ArtistWithAlbumsPayload> getArtist(@PathVariable long artistId) {
        User user = AdminHelper.getLoggedInUser();
        String streamingToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());
        Artist artist = musicManager.getArtist(artistId);
        var t = artistWithAlbumsMapper.toDto(artist, streamingToken);
        return t;
    }

    @DeleteMapping(value = "/{artistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NameValuePayload<String>> deleteArtist(@PathVariable long artistId) {
        try {
            musicManager.deleteArtist(artistId);
        } catch (ContainsMediaItemsException e) {
            return ResponseEntity.badRequest().body(
                    NameValuePayload.<String>builder()
                            .name(JsonNameType.ERROR.name())
                            .value(ErrorCode.CONTAINS_MEDIA.getErrorCode())
                            .build());
        }

        return ResponseEntity.ok().body(
                NameValuePayload.<String>builder()
                        .name(JsonNameType.OUTPUT.name())
                        .value(ValidationUtils.DEFAULT_OK_RESPONSE_MESSAGE)
                        .build());
    }

    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveArtist(@Valid @RequestBody ArtistPayload artistPayload) {

        if (!isArtistNameUnique(artistPayload.getId(), artistPayload.getName())) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_UNIQUE.getErrorCode());
        }

        Artist artist = artistMapper.toDomain(artistPayload);
        musicManager.saveArtist(artist);
        return ResponseEntity.ok().body(ValidationUtils.DEFAULT_OK_RESPONSE_MESSAGE);
    }

    private boolean isArtistNameUnique(long id, String name) {
        Artist artist = musicManager.getArtist(name);
        if (artist == null) {
            return true;
        }

        return artist.getId() == id ? true : false;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArtistPayload> createArtist(@Valid @RequestBody CreateArtistPayload artistPayload) {
        if (musicManager.getArtist(artistPayload.getName()) != null) {
            return ResponseEntity.ok().body(null);
        }

        User user = AdminHelper.getLoggedInUser();
        Artist artist = Artist.builder()
                .name(artistPayload.getName())
                .user(user)
                .build();

        Artist savedArtist = musicManager.saveArtist(artist);
        return ResponseEntity.ok().body(artistMapper.toPayloadList(savedArtist));
    }

}

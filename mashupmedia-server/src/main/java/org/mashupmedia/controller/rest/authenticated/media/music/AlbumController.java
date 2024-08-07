package org.mashupmedia.controller.rest.authenticated.media.music;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.dto.media.music.AlbumWithArtistPayload;
import org.mashupmedia.dto.media.music.AlbumWithTracksAndArtistPayload;
import org.mashupmedia.dto.media.music.CreateAlbumPayload;
import org.mashupmedia.dto.media.music.SaveAlbumPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ErrorPayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.exception.ContainsMediaItemsException;
import org.mashupmedia.exception.NameNotUniqueException;
import org.mashupmedia.mapper.media.music.AlbumMapper;
import org.mashupmedia.mapper.media.music.AlbumWithArtistMapper;
import org.mashupmedia.mapper.media.music.AlbumWithTracksMapper;
import org.mashupmedia.mapper.media.music.CreateAlbumMapper;
import org.mashupmedia.mapper.media.music.SaveAlbumMapper;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private/music/albums")
public class AlbumController {
    private final static int MAX_RANDOM_ALBUMS = 20;
    private final static String FIELD_NAME = "name";

    private final MusicManager musicManager;
    private final MashupMediaSecurityManager mashupMediaSecurityManager;
    private final AlbumWithArtistMapper albumWithArtistMapper;
    private final AlbumWithTracksMapper albumWithTracksMapper;
    private final AlbumMapper albumMapper;
    private final CreateAlbumMapper createAlbumMapper;
    private final SaveAlbumMapper saveAlbumMapper;

    private enum SortAlbum {
        RANDOM
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<AlbumPayload>> createAlbum(
            @RequestBody @Valid CreateAlbumPayload createAlbumPayload) {
        Album album = createAlbumMapper.toDomain(createAlbumPayload);

        ServerResponsePayload<AlbumPayload> serverResponsePayload = ServerResponsePayload.<AlbumPayload>builder()
                .build();
        try {
            musicManager.saveAlbum(album);

        } catch (NameNotUniqueException e) {
            return ResponseEntity.badRequest().body(
                    serverResponsePayload.toBuilder()
                            .errorPayload(ErrorPayload.builder()
                                    .errorCode(ErrorCode.NOT_UNIQUE.getErrorCode())
                                    .build())
                            .build());

        }

        Album savedAlbum = musicManager.getAlbum(album.getId());
        return ResponseEntity.ok().body(
                serverResponsePayload.toBuilder()
                        .payload(albumMapper.toPayload(savedAlbum))
                        .build());
    }

    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<Boolean>> updateAlbum(
            @RequestBody @Valid SaveAlbumPayload albumPayload,
            Errors errors) {

        if (errors.hasErrors()) {
            return ValidationUtils.createResponseEntityPayload(false, errors);
        }

        Album album = saveAlbumMapper.toDomain(albumPayload);
        try {
            musicManager.saveAlbum(album);
        } catch (NameNotUniqueException e) {
            errors.rejectValue(FIELD_NAME, ErrorCode.NOT_UNIQUE.getErrorCode());
        }

        if (errors.hasErrors()) {
            return ValidationUtils.createResponseEntityPayload(false, errors);
        }

        return ResponseEntity.ok().body(
                ServerResponsePayload.<Boolean>builder()
                        .payload(true)
                        .build());
    }

    @DeleteMapping(value = "/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<Boolean>> deleteAlbum(@PathVariable long albumId) {
        try {
            musicManager.deleteAlbum(albumId);
        } catch (ContainsMediaItemsException e) {
            return ResponseEntity.badRequest().body(
                    ServerResponsePayload.<Boolean>builder()
                            .errorPayload(ErrorPayload.builder()
                                    .errorCode(ErrorCode.CONTAINS_MEDIA.getErrorCode())
                                    .build())
                            .payload(false)
                            .build());

        }

        return ResponseEntity
                .ok()
                .body(ServerResponsePayload.<Boolean>builder()
                        .payload(true)
                        .build());

    }

}

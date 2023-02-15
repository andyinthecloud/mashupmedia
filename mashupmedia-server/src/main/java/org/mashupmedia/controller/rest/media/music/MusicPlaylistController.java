package org.mashupmedia.controller.rest.media.music;

import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistType;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.media.music.MusicPlaylistTrackMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/playlist/music")
@RequiredArgsConstructor
public class MusicPlaylistController {

    private final PlaylistManager playlistManager;
    private final MusicManager musicManager;
    private final MusicPlaylistTrackMapper musicPlaylistTrackMapper;
    private final MashupMediaSecurityManager mashupMediaSecurityManager;
    private final LibraryManager libraryManager;

    @GetMapping(value = "/initialised", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> getIsInitialised() {

        User user = AdminHelper.getLoggedInUser();
        Errors errors = new BindException(user, "user");

        if (user == null) {
            errors.reject(ErrorCode.NOT_LOGGED_IN.getErrorCode());
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        if (libraryManager.getLibraries(LibraryType.MUSIC).isEmpty()) {
            errors.reject(ErrorCode.LIBRARIES_UNINITIALISED.getErrorCode());
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @PutMapping(value = "/play-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> playAlbum(@RequestBody long albumId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);
        PlaylistHelper.replacePlaylist(playlist, album.getTracks());
        playlistManager.savePlaylist(playlist);

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @PutMapping(value = "/add-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> addAlbum(@RequestBody long albumId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);
        PlaylistHelper.appendPlaylist(playlist, album.getTracks());
        playlistManager.savePlaylist(playlist);

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @PutMapping(value = "/play-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> playArtist(@RequestBody long artistId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Artist artist = musicManager.getArtist(artistId);
        PlaylistHelper.replacePlaylist(playlist, artist.getAlbums()
                .stream()
                .flatMap(album -> album.getTracks().stream())
                .collect(Collectors.toList()));
        playlistManager.savePlaylist(playlist);

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @PutMapping(value = "/add-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> addArtist(@RequestBody long artistId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Artist artist = musicManager.getArtist(artistId);
        PlaylistHelper.appendPlaylist(playlist, artist.getAlbums()
                .stream()
                .flatMap(album -> album.getTracks().stream())
                .collect(Collectors.toList()));
        playlistManager.savePlaylist(playlist);

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @PutMapping(value = "/navigate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SecureMediaPayload<MusicPlaylistTrackPayload>> navigatePlaylist(
            @RequestBody NavigatePlaylistPayload navigatePlaylistPayload) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }

        PlaylistMediaItem playlistMediaItem = PlaylistHelper.getPlaylistMediaItem(playlist,
                navigatePlaylistPayload.getMediaItemId());

        if (playlistMediaItem == null) {
            int relativeOffset = getRelativeOffset(navigatePlaylistPayload.getNavigatePlaylistType());
            playlistMediaItem = playlistManager.navigateToPlaylistMediaItem(playlist, relativeOffset);
        }

        if (playlistMediaItem == null) {
            return ResponseEntity.noContent().build();
        }

        long cumulativeEndSeconds = PlaylistHelper.getCumulativeEndSeconds(playlist, playlistMediaItem);
        playlistMediaItem.setCumulativeEndSeconds(cumulativeEndSeconds);

        playlistManager.savePlaylist(playlist);
        User user = AdminHelper.getLoggedInUser();
        String mediaToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());
        return ResponseEntity.ok(musicPlaylistTrackMapper.toDto(playlistMediaItem, mediaToken));
    }

    private int getRelativeOffset(NavigatePlaylistType navigatePlaylistType) {
        return switch (navigatePlaylistType) {
            case PREVIOUS -> -1;
            case CURRENT -> 0;
            case NEXT -> 1;
            default -> 0;
        };
    }

    @GetMapping(value = "/playlist-progress/{progressSeconds}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SecureMediaPayload<MusicPlaylistTrackPayload>> getPlaylistTrackByProgress(@PathVariable long progressSeconds) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }

        
        PlaylistMediaItem playlistMediaItem = PlaylistHelper.getPlaylistMediaItemByProgress(playlist, progressSeconds);

        playlistManager.savePlaylist(playlist);
        User user = AdminHelper.getLoggedInUser();
        String mediaToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());

        
        return ResponseEntity.ok(musicPlaylistTrackMapper.toDto(playlistMediaItem, mediaToken));
    }

}

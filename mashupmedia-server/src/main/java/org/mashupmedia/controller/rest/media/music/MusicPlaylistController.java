package org.mashupmedia.controller.rest.media.music;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.TrackWithArtistPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistType;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.media.music.TrackWithArtistMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/playlist/music")
@RequiredArgsConstructor
public class MusicPlaylistController {

    private final PlaylistManager playlistManager;
    private final MusicManager musicManager;
    private final TrackWithArtistMapper trackWithArtistMapper;
    private final MashupMediaSecurityManager mashupMediaSecurityManager;

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
    public ResponseEntity<SecureMediaPayload<TrackWithArtistPayload>> navigatePlaylist(
            @RequestBody NavigatePlaylistPayload navigatePlaylistPayload) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        MediaItem mediaItem = getMediaItemToPlay(navigatePlaylistPayload, playlist);
        if (mediaItem == null) {
            return ResponseEntity.badRequest().build();
        }

        playlistManager.savePlaylist(playlist);
        User user = AdminHelper.getLoggedInUser();
        String mediaToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());
        return ResponseEntity.ok(trackWithArtistMapper.toDto(mediaItem, mediaToken));
    }

    private MediaItem getMediaItemToPlay(NavigatePlaylistPayload navigatePlaylistPayload, Playlist playlist) {

        List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
        if (playlistMediaItems.isEmpty()) {
            return null;
        }

        Optional<PlaylistMediaItem> optionalPlaylistMediaItem = Optional.empty();
        Integer playlistMediaItemId = navigatePlaylistPayload.getMediaItemId();
        if (playlistMediaItemId != null) {
            optionalPlaylistMediaItem = playlist.getAccessiblePlaylistMediaItems().stream()
                    .filter(pmi -> pmi.getId() == playlistMediaItemId)
                    .findAny();
        }

        if (optionalPlaylistMediaItem.isPresent()) {
            optionalPlaylistMediaItem.get().setPlaying(true);
            return optionalPlaylistMediaItem.get().getMediaItem();
        }

        NavigatePlaylistType navigatePlaylistType = navigatePlaylistPayload.getNavigatePlaylistType();
        int relativeOffset = switch (navigatePlaylistType) {
            case PREVIOUS -> -1;
            case CURRENT -> 0;
            case NEXT -> 1;
            default -> 0;
        };

        PlaylistMediaItem playlistMediaItem = PlaylistHelper.navigatePlaylist(playlist, relativeOffset, true);
        return playlistMediaItem.getMediaItem();
    }

}

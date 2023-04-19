package org.mashupmedia.controller.rest.media.playlist;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistType;
import org.mashupmedia.dto.media.playlist.PlaylistActionStatusType;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.media.music.SecureMusicPlaylistTrackMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.service.playlist.PlaylistActionManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/playlist/music")
@RequiredArgsConstructor
public class MusicPlaylistController {

    private final PlaylistManager playlistManager;
    private final MusicManager musicManager;
    private final SecureMusicPlaylistTrackMapper musicPlaylistTrackMapper;
    private final MashupMediaSecurityManager mashupMediaSecurityManager;
    private final LibraryManager libraryManager;
    private final MediaManager mediaManager;
    private final PlaylistActionManager playlistActionManager;

    @PutMapping(value = "/play-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<PlaylistActionStatusType>> playAlbum(@RequestBody long albumId,
            Errors errors) {

        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);

        PlaylistActionStatusType playlistActionStatusType = playlistActionManager.replacePlaylist(playlist.getId(),
                album.getTracks());

        return ValidationUtil.createResponseEntityPayload(playlistActionStatusType, errors);
    }

    @PutMapping(value = "/add-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<PlaylistActionStatusType>> addAlbum(@RequestBody long albumId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);
        PlaylistActionStatusType playlistActionStatusType = playlistActionManager.appendPlaylist(playlist.getId(),
                album.getTracks());

        return ValidationUtil.createResponseEntityPayload(playlistActionStatusType, errors);
    }

    @PutMapping(value = "/play-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<PlaylistActionStatusType>> playArtist(@RequestBody long artistId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Artist artist = musicManager.getArtist(artistId);
        List<Track> tracks = artist.getAlbums()
                .stream()
                .flatMap(album -> album.getTracks().stream())
                .collect(Collectors.toList());

        PlaylistActionStatusType playlistActionStatusType = playlistActionManager.replacePlaylist(
                playlist.getId(),
                tracks);

        return ValidationUtil.createResponseEntityPayload(playlistActionStatusType, errors);
    }

    @PutMapping(value = "/add-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<PlaylistActionStatusType>> addArtist(@RequestBody long artistId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Artist artist = musicManager.getArtist(artistId);
        List<Track> tracks = artist.getAlbums()
                .stream()
                .flatMap(album -> album.getTracks().stream())
                .collect(Collectors.toList());

        PlaylistActionStatusType playlistActionStatusType = playlistActionManager.appendPlaylist(
                playlist.getId(),
                tracks);

        return ValidationUtil.createResponseEntityPayload(playlistActionStatusType, errors);
    }

    @PutMapping(value = "/play-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<PlaylistActionStatusType>> playTrack(@RequestBody long trackId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        MediaItem mediaItem = mediaManager.getMediaItem(trackId);
        if (mediaItem instanceof Track track) {
            PlaylistActionStatusType playlistActionStatusType = playlistActionManager.replacePlaylist(playlist.getId(),
                    track);
            return ValidationUtil.createResponseEntityPayload(playlistActionStatusType, errors);
        } else {
            return ValidationUtil.createResponseEntityPayload(PlaylistActionStatusType.ERROR, errors);
        }
    }

    @PutMapping(value = "/add-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<PlaylistActionStatusType>> addTrack(@RequestBody long trackId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        MediaItem mediaItem = mediaManager.getMediaItem(trackId);
        if (mediaItem instanceof Track track) {
            PlaylistActionStatusType playlistActionStatusType = playlistActionManager.appendPlaylist(playlist.getId(),
                    track);
            return ValidationUtil.createResponseEntityPayload(playlistActionStatusType, errors);
        } else {
            return ValidationUtil.createResponseEntityPayload(PlaylistActionStatusType.ERROR, errors);
        }
    }

    @PutMapping(value = "/navigate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SecureMediaPayload<MusicPlaylistTrackPayload>> navigatePlaylist(
            @RequestBody NavigatePlaylistPayload navigatePlaylistPayload) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }

        PlaylistMediaItem playlistMediaItem = null;

        Long playlistMediaItemId = navigatePlaylistPayload.getPlaylistMediaItemId();
        if (playlistMediaItemId == null) {
            int relativeOffset = getRelativeOffset(navigatePlaylistPayload.getNavigatePlaylistType());
            playlistMediaItem = playlistManager.playRelativePlaylistMediaItem(playlist, relativeOffset);
        } else {
            playlistMediaItem = playlistManager.playPlaylistMediaItem(playlist, playlistMediaItemId);
        }

        if (playlistMediaItem == null) {
            return ResponseEntity.noContent().build();
        }

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

    @GetMapping(value = "/progress/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SecureMediaPayload<MusicPlaylistTrackPayload>> getPlaylistTrackByProgress(
            @PathVariable long playlistId, @RequestParam long progress) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }

        PlaylistMediaItem playlistMediaItem = playlistActionManager.getPlaylistMediaItemByProgress(playlist.getId(),
                progress);
        if (playlistMediaItem != null) {
            playlist.getPlaylistMediaItems().forEach(pmi -> pmi.setPlaying(pmi.equals(playlistMediaItem)));
        }

        playlistManager.savePlaylist(playlist);
        User user = AdminHelper.getLoggedInUser();
        String mediaToken = mashupMediaSecurityManager.generateMediaToken(user.getUsername());

        return ResponseEntity.ok(musicPlaylistTrackMapper.toDto(playlistMediaItem, mediaToken));
    }

    @GetMapping(value = "/tracks/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MusicPlaylistTrackPayload>> getPlaylistTracks(@PathVariable long playlistId) {
        Playlist playlist = playlistManager.getPlaylist(playlistId);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }

        List<MusicPlaylistTrackPayload> musicPlaylistTrackPayloads = playlist.getAccessiblePlaylistMediaItems()
                .stream()
                .map(pmi -> musicPlaylistTrackMapper.toDto(pmi))
                .collect(Collectors.toList());

        return ResponseEntity.ok(musicPlaylistTrackPayloads);
    }

}

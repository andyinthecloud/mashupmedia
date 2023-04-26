package org.mashupmedia.controller.rest.media.playlist;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.dto.media.playlist.EncoderStatusType;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistType;
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
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.service.playlist.PlaylistActionManager;
import org.mashupmedia.task.EncodeMediaItemManager;
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
    private final MediaManager mediaManager;
    private final PlaylistActionManager playlistActionManager;
    private final EncodeMediaItemManager encodeMediaItemManager;

    @PutMapping(value = "/play-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> playAlbum(@RequestBody long albumId,
            Errors errors) {

        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);

        EncoderStatusType encoderStatusType = playlistActionManager.replacePlaylist(playlist.getId(),
                album.getTracks());

        return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
    }

    @PutMapping(value = "/add-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> addAlbum(@RequestBody long albumId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);
        EncoderStatusType encoderStatusType = playlistActionManager.appendPlaylist(playlist.getId(),
                album.getTracks());

        return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
    }

    @PutMapping(value = "/play-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> playArtist(@RequestBody long artistId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Artist artist = musicManager.getArtist(artistId);
        List<Track> tracks = artist.getAlbums()
                .stream()
                .flatMap(album -> album.getTracks().stream())
                .collect(Collectors.toList());

        EncoderStatusType encoderStatusType = playlistActionManager.replacePlaylist(
                playlist.getId(),
                tracks);

        return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
    }

    @PutMapping(value = "/add-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> addArtist(@RequestBody long artistId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Artist artist = musicManager.getArtist(artistId);
        List<Track> tracks = artist.getAlbums()
                .stream()
                .flatMap(album -> album.getTracks().stream())
                .collect(Collectors.toList());

        EncoderStatusType encoderStatusType = playlistActionManager.appendPlaylist(
                playlist.getId(),
                tracks);

        return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
    }

    @PutMapping(value = "/play-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> playTrack(@RequestBody long trackId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        MediaItem mediaItem = mediaManager.getMediaItem(trackId);
        if (mediaItem instanceof Track track) {
            EncoderStatusType encoderStatusType = playlistActionManager.replacePlaylist(playlist.getId(),
                    track);
            return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
        } else {
            return ValidationUtil.createResponseEntityPayload(EncoderStatusType.ERROR, errors);
        }
    }

    @PutMapping(value = "/add-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> addTrack(@RequestBody long trackId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        MediaItem mediaItem = mediaManager.getMediaItem(trackId);
        if (mediaItem instanceof Track track) {
            EncoderStatusType encoderStatusType = playlistActionManager.appendPlaylist(playlist.getId(),
                    track);
            return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
        } else {
            return ValidationUtil.createResponseEntityPayload(EncoderStatusType.ERROR, errors);
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

        EncoderStatusType encoderStatusType = getEncoderStatusType(
                playlistMediaItem.getMediaItem().isEncodedForWeb());
        playlistMediaItem.setEncoderStatusType(encoderStatusType);

        return ResponseEntity.ok(musicPlaylistTrackMapper.toDto(playlistMediaItem, mediaToken));
    }

    private EncoderStatusType getEncoderStatusType(boolean encodedForWeb) {

        if (encodedForWeb) {
            return EncoderStatusType.OK;
        }

        if (!encodeMediaItemManager.isEncoderInstalled()) {
            return EncoderStatusType.ENODER_NOT_INSTALLED;
        }

        return EncoderStatusType.SENT_FOR_ENCODING;
    }

    private int getRelativeOffset(NavigatePlaylistType navigatePlaylistType) {
        return switch (navigatePlaylistType) {
            case PREVIOUS -> -1;
            case CURRENT -> 0;
            case NEXT -> 1;
            default -> 0;
        };
    }

    @GetMapping(value = "/current/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SecureMediaPayload<MusicPlaylistTrackPayload>> getPlaylistTrackByProgress(
            @PathVariable long playlistId) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }

        PlaylistMediaItem playlistMediaItem = playlistManager.playRelativePlaylistMediaItem(
                playlist,
                getRelativeOffset(NavigatePlaylistType.CURRENT));
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

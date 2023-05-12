package org.mashupmedia.controller.rest.media.playlist;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dto.media.SecureMediaPayload;
import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.dto.media.music.MusicQueueAlbumPlaylistPayload;
import org.mashupmedia.dto.media.music.MusicQueueArtistPlaylistPayload;
import org.mashupmedia.dto.media.music.MusicQueuePlaylistPayload;
import org.mashupmedia.dto.media.music.MusicQueueTrackPlaylistPayload;
import org.mashupmedia.dto.media.playlist.EncoderStatusType;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistPayload;
import org.mashupmedia.dto.media.playlist.NavigatePlaylistType;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.media.music.SecureMusicPlaylistTrackMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
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

        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(MashupMediaType.MUSIC);

        Album album = musicManager.getAlbum(albumId);

        EncoderStatusType encoderStatusType = playlistActionManager.replacePlaylist(playlist.getId(),
                album.getTracks());

        return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
    }

    @PutMapping(value = "/add-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> addAlbum(
            @RequestBody MusicQueueAlbumPlaylistPayload musicQueueAlbumPlaylistPayload,
            Errors errors) {

        Playlist playlist = getPlaylist(musicQueueAlbumPlaylistPayload, errors);
        if (playlist == null) {
            return ValidationUtil.createResponseEntityPayload(null, errors);
        }

        Album album = musicManager.getAlbum(musicQueueAlbumPlaylistPayload.getAlbumId());
        EncoderStatusType encoderStatusType = playlistActionManager.appendPlaylist(playlist.getId(),
                album.getTracks());

        return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
    }

    @PutMapping(value = "/play-artist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> playArtist(@RequestBody long artistId,
            Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(MashupMediaType.MUSIC);

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
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> addArtist(
            @RequestBody MusicQueueArtistPlaylistPayload musicQueueArtistPlaylistPayload,
            Errors errors) {

        Playlist playlist = getPlaylist(musicQueueArtistPlaylistPayload, errors);
        if (playlist == null) {
            return ValidationUtil.createResponseEntityPayload(null, errors);
        }

        Artist artist = musicManager.getArtist(musicQueueArtistPlaylistPayload.getArtistId());
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
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(MashupMediaType.MUSIC);
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
    public ResponseEntity<ServerResponsePayload<EncoderStatusType>> addTrack(
            @RequestBody MusicQueueTrackPlaylistPayload musicQueueTrackPlaylistPayload, Errors errors) {
        Playlist playlist = getPlaylist(musicQueueTrackPlaylistPayload, errors);
        if (playlist == null) {
            return ValidationUtil.createResponseEntityPayload(null, errors);
        }

        MediaItem mediaItem = mediaManager.getMediaItem(musicQueueTrackPlaylistPayload.getTrackId());
        if (mediaItem instanceof Track track) {
            EncoderStatusType encoderStatusType = playlistActionManager.appendPlaylist(playlist.getId(),
                    track);
            return ValidationUtil.createResponseEntityPayload(encoderStatusType, errors);
        } else {
            return ValidationUtil.createResponseEntityPayload(EncoderStatusType.ERROR, errors);
        }
    }

    private Playlist getPlaylist(MusicQueuePlaylistPayload musicQueuePlaylistPayload, Errors errors) {
        Playlist playlist;

        Long playlistId = musicQueuePlaylistPayload.getPlaylistId();
        if (playlistId != null) {
            playlist = playlistManager.getPlaylist(playlistId);
            return playlist;
        }

        String name = musicQueuePlaylistPayload.getCreatePlaylistName();
        if (StringUtils.isNotBlank(name)) {
            playlist = Playlist.builder()
                    .name(name)
                    .build();
            playlistManager.savePlaylist(playlist);
            return playlist;
        }

        errors.reject(ErrorCode.PLAYLIST_NOT_FOUND.getErrorCode(),
                "Please either select a playlist or enter a new playlist name");
        return null;

    }

    @PutMapping(value = "/navigate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SecureMediaPayload<MusicPlaylistTrackPayload>> navigatePlaylist(
            @RequestBody NavigatePlaylistPayload navigatePlaylistPayload) {

        Playlist playlist;

        if (navigatePlaylistPayload.getPlaylistId() == null) {
            playlist = playlistManager.getDefaultPlaylistForCurrentUser(MashupMediaType.MUSIC);
        } else {
            playlist = playlistManager.getPlaylist(navigatePlaylistPayload.getPlaylistId());
        }

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
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(MashupMediaType.MUSIC);
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

}

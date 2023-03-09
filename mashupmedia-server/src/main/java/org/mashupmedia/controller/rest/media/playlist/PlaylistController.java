package org.mashupmedia.controller.rest.media.playlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.dto.media.playlist.CreatePlaylistPayload;
import org.mashupmedia.dto.media.playlist.PlaylistActionPayload;
import org.mashupmedia.dto.media.playlist.PlaylistActionTypePayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.media.music.SecureMusicPlaylistTrackMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistManager playlistManager;
    private final SecureMusicPlaylistTrackMapper secureMusicPlaylistTrackMapper;

    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MusicPlaylistTrackPayload>> updatePlaylist(
            @Valid @RequestBody PlaylistActionPayload playlistActionPayload) {
        Playlist playlist = playlistManager.getPlaylist(playlistActionPayload.getPlaylistId());
        Assert.notNull(playlist, "Expecting a playlist");

        List<PlaylistMediaItem> playlistMediaItems = new ArrayList<>(playlist.getPlaylistMediaItems());

        long[] playlistMediaItemIds = playlistActionPayload.getPlaylistMediaItemIds();
        Predicate<PlaylistMediaItem> matchingPredicate = pmi -> Arrays.stream(playlistMediaItemIds)
                .anyMatch(id -> id == pmi.getId());
        List<PlaylistMediaItem> updatePlaylistMediaItems = playlist.getPlaylistMediaItems()
                .stream()
                .filter(matchingPredicate)
                .collect(Collectors.toList());

        PlaylistActionTypePayload playlistActionTypePayload = playlistActionPayload.getPlaylistActionTypePayload();
        if (playlistActionTypePayload == PlaylistActionTypePayload.REMOVE_ITEMS) {
            playlistMediaItems.removeAll(updatePlaylistMediaItems);
        } else if (playlistActionTypePayload == PlaylistActionTypePayload.MOVE_TOP) {
            playlistMediaItems.removeAll(updatePlaylistMediaItems);
            updatePlaylistMediaItems.addAll(playlistMediaItems);
            playlistMediaItems = updatePlaylistMediaItems;
        } else if (playlistActionTypePayload == PlaylistActionTypePayload.MOVE_BOTTOM) {
            playlistMediaItems.removeAll(updatePlaylistMediaItems);
            playlistMediaItems.addAll(updatePlaylistMediaItems);
        }

        List<MediaItem> mediaItems = playlistMediaItems
                .stream()
                .map(pmi -> pmi.getMediaItem())
                .collect(Collectors.toList());

        PlaylistHelper.replacePlaylist(playlist, mediaItems);

        playlistManager.savePlaylist(playlist);

        List<MusicPlaylistTrackPayload> musicPlaylistTrackPayloads = playlist.getAccessiblePlaylistMediaItems()
                .stream()
                .map(pmi -> secureMusicPlaylistTrackMapper.toDto(pmi))
                .collect(Collectors.toList());

        return ResponseEntity.ok(musicPlaylistTrackPayloads);
    }

    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> deletePlaylist(@PathVariable long playlistId, Errors errors) {
        User user = AdminHelper.getLoggedInUser();
        Assert.notNull(user, "User should not be null");

        Playlist playlist = playlistManager.getPlaylist(playlistId);
        if (!user.isAdministrator() || !playlist.getCreatedBy().equals(user) ) {
            throw new SecurityException("Only an administrator or the playlist creator can delete the playlist");
        }

        playlistManager.deletePlaylist(playlistId);
        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> createPlaylist(@Valid @RequestBody CreatePlaylistPayload createPlaylistPayload, Errors errors) {
        User user = AdminHelper.getLoggedInUser();
        Assert.notNull(user, "User should not be null");

        Playlist playlist = new Playlist();
        playlist.setName(createPlaylistPayload.getName());
        playlist.setCreatedBy(user);
        playlistManager.savePlaylist(playlist);

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

}
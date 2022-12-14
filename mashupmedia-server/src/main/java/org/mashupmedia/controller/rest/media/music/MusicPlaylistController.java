package org.mashupmedia.controller.rest.media.music;

import java.util.stream.Collectors;

import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
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

    @PutMapping(value = "/play-album", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> playAlbum(@RequestBody long albumId, Errors errors) {
        Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

        Album album = musicManager.getAlbum(albumId);
        PlaylistHelper.replacePlaylist(playlist, album.getTracks());

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

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }



}

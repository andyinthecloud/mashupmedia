package org.mashupmedia.controller.rest.authenticated.media.playlist;

import java.util.Date;
import java.util.List;

import org.mashupmedia.component.TranscodeConfigurationComponent;
import org.mashupmedia.dto.media.playlist.PlaylistMediaItemTranscodePayload;
import org.mashupmedia.dto.media.playlist.TranscodeStatusType;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private/playlist/transcode")
@RequiredArgsConstructor
public class TranscodeStatusController {

    private final PlaylistManager playlistManager;
    private final TranscodeConfigurationComponent transcodeConfigurationComponent;

    @GetMapping(value = "/status/{playlistId}/{fromDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlaylistMediaItemTranscodePayload> getPlaylistTranscodeStatus(long playlistId, Date fromDate) {
        Playlist playlist = playlistManager.getPlaylist(playlistId);
        Assert.notNull(playlist, "playlist should not be null");

        User user = AdminHelper.getLoggedInUser();
        List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems(user);
        List<PlaylistMediaItemTranscodePayload> transcodeItems = playlistMediaItems.stream()
                .filter(pmi -> pmi.getMediaItem().getUpdatedOn().after(fromDate))
                .map(pmi -> PlaylistMediaItemTranscodePayload.builder()
                        .playlistMediaItemId(pmi.getId())ó
                        .transcodeStatusType(getTranscodeStatusType(pmi))
                        .build())
                .collect(Collectors.toList());

        return transcodeItems;
    }

    private TranscodeStatusType getTranscodeStatusType(PlaylistMediaItem playlistMediaItem) {
        MediaItem mediaItem = playlistMediaItem .getMediaItem();
        if (mediaItem instanceof Track track) {
            MediaContentType audioMediaContentType = transcodeConfigurationComponent.getTranscodeAudioMediaContentType();
            MediaResource mediaResource = track.getMediaResource(audioMediaContentType);
            return mediaResource == null ? TranscodeStatusType.TRANSCODING : TranscodeStatusType.TRANSCODED;
        }

        return null;
    }

}

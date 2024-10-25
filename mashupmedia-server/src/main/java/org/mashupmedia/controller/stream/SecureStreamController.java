package org.mashupmedia.controller.stream;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.awaitility.Awaitility;
import org.mashupmedia.component.TranscodeConfigurationComponent;
import org.mashupmedia.controller.stream.resource.MediaResourceHttpRequestHandler;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.service.storage.StorageManager;
import org.mashupmedia.service.transcode.TranscodeAudioManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/stream/secure")
@RequiredArgsConstructor
@Slf4j
public class SecureStreamController {

    private final MashupMediaSecurityManager securityManager;
    private final MediaResourceHttpRequestHandler mediaResourceHttpRequestHandler;
    private final PlaylistManager playlistManager;
    private final StorageManager storageManager;
    private final TranscodeAudioManager transcodeAudioManager;
    private final TranscodeConfigurationComponent transcodeConfigurationComponent;
    private final MediaManager mediaManager;

    @RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.GET)
    public void streamMedia(@Valid @PathVariable Long mediaItemId,
            @RequestParam String mediaToken,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        mediaResourceHttpRequestHandler.handleRequest(request, response);
    }

    @RequestMapping(value = "/playlist/{playlistId}", method = RequestMethod.GET)
    public void streamPlaylist(@Valid @PathVariable Long playlistId,
            @RequestParam String mediaToken,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Playlist playlist = playlistManager.getPlaylist(playlistId);
        List<PlaylistMediaItem> unplayedMediaItems = getUnplayedPlaylistMediaItems(playlist);
        response.setHeader("Transfer-Encoding", "chunked");
        response.setContentType(transcodeConfigurationComponent.getTranscodeAudioMediaContentType().getMimeType());

        for (PlaylistMediaItem playlistMediaItem : unplayedMediaItems) {

            if (!isInPlaylistMediaItems(unplayedMediaItems, playlistMediaItem)) {
                continue;
            }

            MediaItem mediaItem = playlistMediaItem.getMediaItem();
            // if
            // (!mediaItem.isTranscoded(transcodeConfigurationComponent.getTranscodeAudioMediaContentType()))
            // {
            // continue;
            // }

            playlist.getPlaylistMediaItems().forEach(pmi -> pmi.setPlaying(pmi.equals(playlistMediaItem)));
            playlistManager.savePlaylist(playlist);

            if (mediaItem instanceof Track) {
                streamTrack(playlistId, playlistMediaItem, response);
            }
        }
    }

    private void streamTrack(long playlistId, PlaylistMediaItem playlistMediaItem, HttpServletResponse response) {

        Track track = (Track) playlistMediaItem.getMediaItem();
        long mediaItemId = track.getId();

        log.info("Streaming: playing track: " + track.getTitle());
        boolean isStreamingTrack = false;

        LocalDateTime endTrackDateTime = LocalDateTime.now();
        endTrackDateTime = endTrackDateTime.plusSeconds(track.getTrackLength());

        MediaContentType transcodeAudioMediaContentType = transcodeConfigurationComponent
                .getTranscodeAudioMediaContentType();
        MediaResource mediaResource = track
                .getMediaResource(transcodeAudioMediaContentType);
        if (mediaResource == null) {
            log.info("Cannot find supported media file, will send for transcoding");
            transcodeAudioManager.processTrack(track, track.getOriginalMediaResource().getPath());

            Awaitility.await()
                    .timeout(60, TimeUnit.SECONDS)
                    .pollDelay(5, TimeUnit.SECONDS)
                    .until(() -> {
                        MediaItem mediaItem = mediaManager.getMediaItem(track.getId());
                        return mediaItem.isTranscoded(transcodeAudioMediaContentType);
                    });
        }

        MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        mediaResource = mediaItem.getMediaResource(transcodeAudioMediaContentType);
        if (mediaResource == null) {
            log.error("No transcoded media resource found");
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = storageManager.getInputStream(mediaResource.getPath());
            isStreamingTrack = true;
            IOUtils.copy(inputStream, response.getOutputStream());

        } catch (IOException e) {
            log.error("Streaming: error copying media to output stream", e);
            return;
        }
        finally {
            IOUtils.closeQuietly(inputStream);
            isStreamingTrack = false;
        }

        int sleepCount = 0;
        while (LocalDateTime.now().isBefore(endTrackDateTime) || isStreamingTrack) {
            try {
                sleepCount++;
                if (sleepCount % 10 == 0) {
                    if (!isCurrentlyPlaying(playlistId, playlistMediaItem)) {
                        log.debug("Streaming: " + track.getTitle() + " is NOT in playlist, sleepCount = " + sleepCount);
                        return;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Streaming: interrupted sleeping", e);
                return;
            }
        }
    }

    private boolean isCurrentlyPlaying(@Valid Long playlistId, PlaylistMediaItem playlistMediaItem) {
        Playlist playlist = playlistManager.getPlaylist(playlistId);
        PlaylistMediaItem currentPlaylistMediaItem = playlistManager.playRelativePlaylistMediaItem(playlist, 0);
        return currentPlaylistMediaItem.equals(playlistMediaItem);
    }

    private List<PlaylistMediaItem> getUnplayedPlaylistMediaItems(Playlist playlist) {
        PlaylistMediaItem currenPlaylistMediaItem = playlistManager.playRelativePlaylistMediaItem(playlist, 0);
        List<PlaylistMediaItem> playlistMediaItems = playlist
                .getAccessiblePlaylistMediaItems(AdminHelper.getLoggedInUser());
        int index = playlistMediaItems.indexOf(currenPlaylistMediaItem);
        return playlistMediaItems.subList(index, playlistMediaItems.size());
    }

    private boolean isInPlaylistMediaItems(List<PlaylistMediaItem> playlistMediaItems,
            PlaylistMediaItem playlistMediaItem) {
        return playlistMediaItems
                .stream()
                .anyMatch(pmi -> pmi.equals(playlistMediaItem));
    }

}

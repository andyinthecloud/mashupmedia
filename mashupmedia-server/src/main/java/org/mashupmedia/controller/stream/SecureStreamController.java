package org.mashupmedia.controller.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mashupmedia.controller.stream.resource.MediaResourceHttpRequestHandler;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.task.EncodeMediaItemManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
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
    private final MediaManager mediaManager;
    private final EncodeMediaItemManager encodeMediaItemManager;

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
        response.setContentType(MediaContentType.AUDIO_MP3.getContentType());

        LocalDateTime endTrackDateTime = LocalDateTime.now();

        for (PlaylistMediaItem playlistMediaItem : unplayedMediaItems) {

            if (playlistMediaItem.getMediaItem() instanceof Track track && track.isEncodedForWeb()) {

                if (!isInPlaylistMediaItems(unplayedMediaItems, playlistMediaItem)) {
                    return;
                }

                log.info("Streaming: playing track: " + track.getTitle());
                boolean isStreamingTrack = false;

                endTrackDateTime = endTrackDateTime.plusSeconds(track.getTrackLength());

                playlist.getPlaylistMediaItems().forEach(pmi -> pmi.setPlaying(pmi.equals(playlistMediaItem)));
                playlistManager.savePlaylist(playlist);

                FileInputStream fileInputStream = null;
                try {
                    File mediaFile = track.getStreamingFile();
                    if (mediaFile.isFile()) {
                        isStreamingTrack = true;
                        fileInputStream = new FileInputStream(track.getStreamingFile());
                        IOUtils.copy(fileInputStream, response.getOutputStream());
                    } else {
                        log.info("Streaming: cannot find media file, will send for encoding");
                        track.getMediaEncodings().clear();
                        MediaEncoding mediaEncoding = MediaItemHelper.createMediaEncoding(track.getFileName());
                        track.getMediaEncodings().add(mediaEncoding);
                        mediaManager.saveMediaItem(track);
                        encodeMediaItemManager.processMediaItemForEncoding(track, MediaContentType.AUDIO_MP3);
                    }

                } catch (IOException e) {
                    log.error("Streaming: error copying media to output stream", e);
                    return;
                } catch (MediaItemEncodeException e) {
                    log.error("Streaming: error encoding media", e);
                    endTrackDateTime = endTrackDateTime.minusSeconds(track.getTrackLength());
                    continue;
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                    isStreamingTrack = false;
                }

                int sleepCount = 0;
                while (LocalDateTime.now().isBefore(endTrackDateTime) || isStreamingTrack) {
                    try {
                        sleepCount++;
                        if (sleepCount % 10 == 0) {
                            if(!isCurrentlyPlaying(playlistId, playlistMediaItem)) {
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
        }
    }

    private boolean isCurrentlyPlaying(@Valid Long playlistId, PlaylistMediaItem playlistMediaItem) {
        Playlist playlist = playlistManager.getPlaylist(playlistId);
        PlaylistMediaItem currentPlaylistMediaItem = playlistManager.playRelativePlaylistMediaItem(playlist, 0);
        return currentPlaylistMediaItem.equals(playlistMediaItem);
	}

	private List<PlaylistMediaItem> getUnplayedPlaylistMediaItems(Playlist playlist) {
        PlaylistMediaItem currenPlaylistMediaItem = playlistManager.playRelativePlaylistMediaItem(playlist, 0);
        List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
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

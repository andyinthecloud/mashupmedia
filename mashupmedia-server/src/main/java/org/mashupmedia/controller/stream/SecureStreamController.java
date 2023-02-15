package org.mashupmedia.controller.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.coyote.CloseNowException;
import org.mashupmedia.controller.stream.resource.MediaResourceHttpRequestHandler;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    public ResponseEntity<StreamingResponseBody> streamPlaylist(@Valid @PathVariable Long playlistId,
            @RequestParam String mediaToken,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Playlist playlist = playlistManager.getPlaylist(playlistId);

        List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();

        PlaylistMediaItem currenPlaylistMediaItem = playlistManager.navigateToPlaylistMediaItem(playlist, 0);
        int index = playlistMediaItems.indexOf(currenPlaylistMediaItem);
        List<PlaylistMediaItem> unplayedMediaItems = playlistMediaItems.subList(index, playlistMediaItems.size() - 1);

        StreamingResponseBody streamingResponseBody = out -> {
            unplayedMediaItems.forEach(playlistMediaItem -> {
                MediaItem mediaItem = playlistMediaItem.getMediaItem();
                File file = new File(mediaItem.getPath());

                if (file.isFile()) {
                    Path path = file.toPath();
                    try {
                        Files.copy(path, out);
                    } catch (IOException e) {
                        log.error("Error closing media stream", e);
                    }
                }
            });
            IOUtils.closeQuietly(out);
        };

        response.setContentType(MediaContentType.MP3.getMimeContentType());

        return ResponseEntity.ok().body(streamingResponseBody);
    }

}

package org.mashupmedia.controller.stream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mashupmedia.controller.stream.resource.MediaResourceHttpRequestHandler;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem.MashupMediaType;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.PlaylistManager;
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

@Controller
@RequestMapping("/stream/secure")
@RequiredArgsConstructor
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
    public void streamPlaylist(@Valid @PathVariable Long playlistId,
            @RequestParam String mediaToken,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Playlist playlist = playlistManager.getPlaylist(playlistId);

        List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();

        PlaylistMediaItem currenPlaylistMediaItem = playlistManager.playRelativePlaylistMediaItem(playlist, 0);
        int index = playlistMediaItems.indexOf(currenPlaylistMediaItem);
        List<PlaylistMediaItem> unplayedMediaItems = playlistMediaItems.subList(index, playlistMediaItems.size() - 1);

        // MediaEncoding mediaEncoding =
        // unplayedMediaItems.get(0).getMediaItem().getBestMediaEncoding();

        // List<FileInputStream> fileInputStreams = new ArrayList<>();

        boolean isStreaming = false;

        for (PlaylistMediaItem pmi : unplayedMediaItems) {
            if (pmi.getMediaItem() instanceof Track track) {

                MediaEncoding mediaEncoding = track.getBestMediaEncoding();
                if (mediaEncoding.getMediaContentType() == MediaContentType.AUDIO_MP3) {
                    isStreaming = true;
                    IOUtils.copyLarge(new FileInputStream(track.getPath()), response.getOutputStream());

                }
                // fileLength += track.getSizeInBytes();
                // fileInputStreams.add(new FileInputStream(track.getPath()));
                // MediaEncoding mediaEncoding = track.getBestMediaEncoding();
                // response.setContentType(mediaEncoding.getMediaContentType().getMimeContentType());
            }
        }

        response.setContentType(MediaContentType.AUDIO_MP3.getContentType());

        if (isStreaming) {
            response.setHeader("Transfer-Encoding", "chunked");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        }

        // response.setHeader("Accept-Ranges", "");

        // response.setContentLengthLong(fileLength);

        // Enumeration<FileInputStream> fileInputStreamsEnumeration =
        // Collections.enumeration(fileInputStreams);
        // SequenceInputStream sequenceInputStream = new
        // SequenceInputStream(fileInputStreamsEnumeration);

        // IOUtils.copyLarge(sequenceInputStream, response.getOutputStream());
        // response.flushBuffer();

    }

}

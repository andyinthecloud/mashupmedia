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
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.PlaylistManager;
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
        long fileLength = 0;
        List<FileInputStream> fileInputStreams = new ArrayList<>();
        for (PlaylistMediaItem pmi : unplayedMediaItems) {
            if (pmi.getMediaItem() instanceof Track track) {
                fileLength += track.getSizeInBytes();
                fileInputStreams.add(new FileInputStream(track.getPath()));
            }
        }

        response.setContentType(MediaContentType.MP3.getMimeContentType());
        response.setContentLengthLong(fileLength);

        Enumeration<FileInputStream> fileInputStreamsEnumeration = Collections.enumeration(fileInputStreams);
        SequenceInputStream sequenceInputStream = new SequenceInputStream(fileInputStreamsEnumeration);

        IOUtils.copy(sequenceInputStream, response.getOutputStream());
        response.flushBuffer();

    }

}

package org.mashupmedia.controller.stream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.mashupmedia.controller.stream.resource.MediaResourceHttpRequestHandler;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<InputStreamResource> streamPlaylist(@Valid @PathVariable Long playlistId,
            @RequestParam String mediaToken,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Playlist playlist = playlistManager.getPlaylist(playlistId);

        List<InputStream> inputStreams = playlist.getAccessiblePlaylistMediaItems()
                .stream()
                .map(pmi -> pmi.getMediaItem())
                .map(mi -> getInputStream(mi.getPath()))
                .filter(is -> is != null)
                .collect(Collectors.toList());

        Enumeration<InputStream> enumeration = Collections.enumeration(inputStreams);
        InputStream inputStream = new SequenceInputStream(enumeration);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        response.setContentType(MediaContentType.MP3.getMimeContentType());
        return ResponseEntity.ok().body(inputStreamResource);
    }

    private InputStream getInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}

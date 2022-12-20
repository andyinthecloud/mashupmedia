package org.mashupmedia.controller.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mashupmedia.controller.stream.resource.MediaResourceHttpRequestHandler;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MashupMediaSecurityManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/stream/secure")
@RequiredArgsConstructor
public class SecureStreamController {

    // https://stackoverflow.com/questions/20634603/how-do-i-return-a-video-with-spring-mvc-so-that-it-can-be-navigated-using-the-ht

    // private final MediaManager mediaManager;

    private final MashupMediaSecurityManager securityManager;

    private final MediaResourceHttpRequestHandler mediaResourceHttpRequestHandler;

    @RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.GET)
    public void streamMedia(@PathVariable Long mediaItemId,
            @RequestParam String mediaToken,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!securityManager.isMediaTokenValid(mediaToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        // MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        // File file = new File(mediaItem.getPath());

        // mediaResourceHttpRequestHandler.getResource(request);
        mediaResourceHttpRequestHandler.handleRequest(request, response);

        // MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaItem.getFormat());
        // response.setContentType(mediaContentType.getMimeContentType());
        // // response.setHeader("Content-Disposition", "inline; filename=" +
        // // file.getName());
        // response.setHeader("Content-Length", String.valueOf(file.length()));
        // return new FileSystemResource(file);
    }
}

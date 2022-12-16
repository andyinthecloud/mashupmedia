package org.mashupmedia.controller.stream;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletResponse;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/stream")
@RequiredArgsConstructor
public class StreamController {

    private final MediaManager mediaManager;

    @RequestMapping(value = "/media", method = RequestMethod.GET)
    public @ResponseBody Resource streamMedia(@RequestParam(value = "id") Long mediaItemId,
            HttpServletResponse response) throws FileNotFoundException {

        MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
        File file = new File(mediaItem.getPath());

        MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaItem.getFormat());
        response.setContentType(mediaContentType.getMimeContentType());
        // response.setHeader("Content-Disposition", "inline; filename=" +
        // file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }
}

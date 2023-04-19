package org.mashupmedia.controller.rest.encode;

import java.io.File;
import java.io.IOException;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.encode.FfMpegManager;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/encode")
@RequiredArgsConstructor
@Slf4j
public class EncodeController {

    private final FfMpegManager ffMpegManager;
    private final ConfigurationManager configurationManager;

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NameValuePayload<String>> get() {

        String pathToFfmpeg = configurationManager.getConfigurationValue(MashUpMediaConstants.FFMPEG_PATH);

        NameValuePayload<String> ffMpegPayload = NameValuePayload.<String>builder()
                .name("path")
                .value(pathToFfmpeg)
                .build();

        return ResponseEntity.ok().body(ffMpegPayload);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> post(@Valid @RequestBody NameValuePayload<String> ffMpegPayload,
            Errors errors) {
        configurationManager.saveConfiguration(MashUpMediaConstants.FFMPEG_PATH, ffMpegPayload.getValue());
        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/verify-installation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> verifyInstallation(@RequestParam String path) {
        File ffMpegExecutableFile = new File(path);

        boolean isFfmpegInstalled = false;

        try {
            isFfmpegInstalled = ffMpegManager.isValidFfMpeg(ffMpegExecutableFile);
        } catch (IOException e) {
            log.info("Invalid ffmpeg path", e);
        }

        return ResponseEntity.ok().body(isFfmpegInstalled);
    }
}

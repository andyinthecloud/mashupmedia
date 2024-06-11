package org.mashupmedia.controller.rest.authenticated.encode;

import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.task.EncodeMediaItemManager;
import org.mashupmedia.util.ValidationUtils;
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

@RestController
@RequestMapping("/api/private/encode")
@RequiredArgsConstructor
public class EncodeController {

    private final EncodeMediaItemManager encodeMediaItemManager;

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NameValuePayload<String>> get() {

        String encoderPath = encodeMediaItemManager.getEncoderPath();

        NameValuePayload<String> ffMpegPayload = NameValuePayload.<String>builder()
                .name("path")
                .value(encoderPath)
                .build();

        return ResponseEntity.ok().body(ffMpegPayload);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> post(
            @Valid @RequestBody NameValuePayload<String> ffMpegPayload,
            Errors errors) {
        encodeMediaItemManager.saveEncoderPath(ffMpegPayload.getValue());
        return ValidationUtils.createResponseEntityPayload(ValidationUtils.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/verify-installation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> verifyInstallation(@RequestParam String path) {
        return ResponseEntity.ok().body(encodeMediaItemManager.isEncoderInstalled(path));
    }
}

package org.mashupmedia.controller.rest.authenticated.media.social;

import org.mashupmedia.dto.media.social.VoteMediaItemPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.service.social.VoteManager;
import org.mashupmedia.util.ValidationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private/vote")
@RequiredArgsConstructor
public class VoteController {

    private final static String FIELD_MEDIA_ITEM_ID = "mediaItemId";

    private final VoteManager voteManager;

    @PostMapping(value = "/media-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<Boolean>> voteMediaItem(
            @RequestBody @Valid VoteMediaItemPayload voteMediaItemPayload,
            Errors errors) {

        long mediaItemId = voteMediaItemPayload.getMediaItemId();

        if (voteManager.isVotingDisabled(mediaItemId)) {
            errors.rejectValue(FIELD_MEDIA_ITEM_ID, ErrorCode.VOTING_DISABLED.getErrorCode());
            return ValidationUtils.createResponseEntityPayload(false, errors);
        }

        voteManager.voteMediaItem(mediaItemId);
        return ResponseEntity.ok().body(
                ServerResponsePayload.<Boolean>builder()
                        .payload(true)
                        .build()
        );
    }

}

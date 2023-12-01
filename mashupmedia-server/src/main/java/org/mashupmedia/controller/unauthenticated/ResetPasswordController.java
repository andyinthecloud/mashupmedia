package org.mashupmedia.controller.unauthenticated;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.mashupmedia.dto.admin.ResetPasswordPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.EmailService;
import org.mashupmedia.util.ActivationTokenUtils;
import org.mashupmedia.util.EncryptService;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final static String FIELD_NAME_USERNAME = "username";

    private final AdminManager adminManager;
    private final EmailService emailService;
    private final EncryptService encryptService;

    @PutMapping(value = "/step-reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<ResetPasswordPayload>> stepReset(
            @Valid @RequestBody ResetPasswordPayload resetPasswordPayload,
            Errors errors) {

        validateUserEnteredPayload(resetPasswordPayload, errors);

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(resetPasswordPayload, errors);
        }

        String username = resetPasswordPayload.getUsername();
        String activationCode = ActivationTokenUtils.generateActivationCode();

        emailService.sendUserResetPasswordMail(username, activationCode);
        String rawToken = ActivationTokenUtils.generateRawToken(username, activationCode);

        ResetPasswordPayload createUserPayloadWithToken = ResetPasswordPayload.builder()
                .username(resetPasswordPayload.getUsername())
                .password(resetPasswordPayload.getPassword())
                .token(encryptService.encrypt(rawToken))
                .build();

        return ValidationUtil.createResponseEntityPayload(createUserPayloadWithToken, errors);
    }

    private void validateUserEnteredPayload(ResetPasswordPayload resetPasswordPayload, Errors errors) {
        String username = resetPasswordPayload.getUsername();
        if (adminManager.getUser(username) == null) {
            errors.rejectValue(FIELD_NAME_USERNAME,
                    ErrorCode.NOT_UNIQUE.getErrorCode(),
                    "User not found ");
        }

        if (!EmailValidator.getInstance().isValid(username)) {
            errors.rejectValue("username",
                    ErrorCode.EMAIL_INVALID.getErrorCode(),
                    "Invalid email");
        }
    }

    @PutMapping(value = "/step-activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> stepActivate(
            @Valid @RequestBody ResetPasswordPayload resetPasswordPayload,
            Errors errors) {

        validateUserEnteredPayload(resetPasswordPayload, errors);

        if (StringUtils.isBlank(resetPasswordPayload.getToken())) {
            errors.rejectValue(ActivationTokenUtils.FIELD_NAME_ACTIVATION_CODE,
                    ErrorCode.TOKEN_INVALID.getErrorCode(),
                    ActivationTokenUtils.ERROR_TOKEN);
        }
        
        String decryptedToken = encryptService.decrypt(resetPasswordPayload.getToken());
        ActivationTokenUtils.validateToken(decryptedToken, resetPasswordPayload.getUsername(), resetPasswordPayload.getActivationCode(),  errors);

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        adminManager.updatePassword(resetPasswordPayload.getUsername(), resetPasswordPayload.getPassword());

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }
}

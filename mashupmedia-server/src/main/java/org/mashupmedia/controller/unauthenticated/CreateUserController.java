package org.mashupmedia.controller.unauthenticated;

import org.apache.commons.validator.routines.EmailValidator;
import org.mashupmedia.dto.admin.CreateUserPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.CreateUserMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.EmailService;
import org.mashupmedia.util.ActivationTokenUtils;
import org.mashupmedia.util.EncryptService;
import org.mashupmedia.util.ValidationUtil;
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
@RequestMapping("/api/public/create-user")
@RequiredArgsConstructor
public class CreateUserController {

    private final static String FIELD_NAME_USERNAME = "username";

    private final AdminManager adminManager;
    private final EncryptService encryptService;
    private final CreateUserMapper createUserMapper;
    private final EmailService emailService;

    @PostMapping(value = "/step-create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<CreateUserPayload>> stepCreate(
            @Valid @RequestBody CreateUserPayload createUserPayload,
            Errors errors) {

        validateUserEnteredPayload(createUserPayload, errors);

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(createUserPayload, errors);
        }

        String username = createUserPayload.getUsername();
        String activationCode = ActivationTokenUtils.generateActivationCode();

        emailService.sendUserActivationMail(username, activationCode);
        String rawToken = ActivationTokenUtils.generateRawToken(username, activationCode);
        CreateUserPayload createUserPayloadWithToken = createUserPayload.toBuilder()
                .name(createUserPayload.getName())
                .username(createUserPayload.getUsername())
                .password(createUserPayload.getPassword())
                .token(encryptService.encrypt(rawToken))
                .build();

        return ValidationUtil.createResponseEntityPayload(createUserPayloadWithToken, errors);
    }

    private void validateUserEnteredPayload(CreateUserPayload createUserPayload, Errors errors) {
        String username = createUserPayload.getUsername();

        User user = adminManager.getUser(username);
        if (user != null && user.isValidated()) {
            errors.rejectValue(FIELD_NAME_USERNAME,
                    ErrorCode.NOT_UNIQUE.getErrorCode(),
                    "A user with this email already exists");
            return;
        }

        if (!EmailValidator.getInstance().isValid(username)) {
            errors.rejectValue("username",
                    ErrorCode.EMAIL_INVALID.getErrorCode(),
                    "Invalid email");
        }

    }

    @PostMapping(value = "/step-activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> stepActivate(
            @Valid @RequestBody CreateUserPayload createUserPayload,
            Errors errors) {

        validateUserEnteredPayload(createUserPayload, errors);
        String decryptedToken = encryptService.decrypt(createUserPayload.getToken());
        ActivationTokenUtils.validateToken(decryptedToken, createUserPayload.getUsername(),
                createUserPayload.getActivationCode(), errors);

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        adminManager.saveUser(createUserMapper.toDomain(createUserPayload));

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

}

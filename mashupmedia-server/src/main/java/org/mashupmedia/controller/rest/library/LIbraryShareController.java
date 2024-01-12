package org.mashupmedia.controller.rest.library;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.EmailValidator;
import org.mashupmedia.dto.library.LibrarySharePayload;
import org.mashupmedia.dto.library.LibraryShareUserPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.library.LibraryShareUserMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.EmailService;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private/library/share")
public class LIbraryShareController {
    private static final String FIELD_NAME_EMAIL = "email";

    private final LibraryShareUserMapper libraryShareUserMapper;
    private final LibraryManager libraryManager;
    private final EmailService emailService;

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<List<LibraryShareUserPayload>>> addShare(
            @Valid @RequestBody LibrarySharePayload librarySharePayload, Errors errors) {

        String email = librarySharePayload.getEmail();
        if (!EmailValidator.getInstance().isValid(email)) {
            errors.rejectValue(
                    FIELD_NAME_EMAIL,
                    ErrorCode.EMAIL_INVALID.getErrorCode(),
                    "The email address is invalid");
        }

        long libraryId = librarySharePayload.getLibraryId();
        if (!errors.hasErrors()) {
            libraryManager.addUserShare(email, libraryId);
            User user = AdminHelper.getLoggedInUser();
            emailService.sendAddLibraryShareEmail(user.getName(), email);
        }

        List<User> shareUsers = libraryManager.getShareUsers(libraryId);
        List<LibraryShareUserPayload> libraryShareUserPayloads = shareUsers.stream()
                .map(libraryShareUserMapper::toDto)
                .collect(Collectors.toList());

        return ValidationUtil.createResponseEntityPayload(libraryShareUserPayloads, errors);
    }

    @GetMapping(value = "/{libraryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LibraryShareUserPayload>> getShareUsers(
            @PathVariable long libraryId) {
        List<User> shareUsers = libraryManager.getShareUsers(libraryId);
        List<LibraryShareUserPayload> libraryShareUserPayloads = shareUsers.stream()
                .map(libraryShareUserMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(libraryShareUserPayloads);
    }

    @DeleteMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LibraryShareUserPayload>> deleteShare(
            @Valid @RequestBody LibrarySharePayload deleteLibrarySharePayload) {

        long libraryId = deleteLibrarySharePayload.getLibraryId();
        String username = deleteLibrarySharePayload.getEmail();
        libraryManager.deleteShareUser(libraryId, username);
        List<User> shareUsers = libraryManager.getShareUsers(libraryId);

        List<LibraryShareUserPayload> libraryShareUserPayloads = shareUsers.stream()
                .map(libraryShareUserMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(libraryShareUserPayloads);
    }

}

package org.mashupmedia.controller.rest.library;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.library.LibraryNameValuePayload;
import org.mashupmedia.dto.library.LibraryPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.LibraryMapper;
import org.mashupmedia.mapper.LibraryNameValueMapper;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.task.LibraryUpdateTaskManager;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private static final String FIELD_NAME_VALUE = "value";
    private static final String FIELD_NAME_PATH = "path";

    private final LibraryMapper libraryMapper;
    private final LibraryNameValueMapper libraryNameValueMapper;
    private final LibraryManager libraryManager;
    private final LibraryUpdateTaskManager libraryUpdateTaskManager;

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LibraryNameValuePayload>> getLibraries() {

        List<LibraryNameValuePayload> libraryNameValuePayloads = libraryManager.getLibraries(LibraryType.ALL)
                .stream()
                .map(libraryNameValueMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(libraryNameValuePayloads);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/{libraryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LibraryPayload> getLibrary(@PathVariable long libraryId) {
        LibraryPayload libraryPayload = libraryMapper.toDto(libraryManager.getLibrary(libraryId));
        return ResponseEntity.ok().body(libraryPayload);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping(value = "/{libraryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteLibrary(@PathVariable long libraryId) {
        libraryManager.deleteLibrary(libraryId);
        return ResponseEntity.ok().body(true);        
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> saveGroup(@Valid @RequestBody LibraryPayload libraryPayload,
            Errors errors) {

        if (isInvalidLibraryPath(libraryPayload.getPath())) {
            errors.rejectValue(
                    FIELD_NAME_PATH,
                    ErrorCode.LIBRARY_INVALID_PATH.getErrorCode(),
                    "The library path is invalid");
        }

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        Library library = libraryMapper.toDomain(libraryPayload);
        libraryManager.saveLibrary(library);
        libraryManager.saveAndReinitialiseLibrary(library);
        libraryUpdateTaskManager.updateLibrary(library);

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    private boolean isInvalidLibraryPath(String libraryPath) {
        if (StringUtils.isBlank(libraryPath)) {
            return true;
        }

        File file = new File(libraryPath);
        return !file.isDirectory();
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping(value = "/check-path", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> isValidPath(
            @Valid @RequestBody NameValuePayload<String> nameValuePayload, Errors errors) {

        if (isInvalidLibraryPath(nameValuePayload.getValue())) {
            errors.rejectValue(
                    FIELD_NAME_VALUE,
                    ErrorCode.LIBRARY_INVALID_PATH.getErrorCode(),
                    "The library path is invalid");
        }

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }


    

}

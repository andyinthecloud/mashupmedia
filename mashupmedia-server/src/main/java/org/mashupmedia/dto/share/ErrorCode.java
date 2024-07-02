package org.mashupmedia.dto.share;

public enum ErrorCode {

    NOT_UNIQUE("error.notUnique"),
    INCORRECT_PASSWORD("error.password.incorrect"),
    NON_MATCHING_PASSWORDS("error.password.matching"),
    LIBRARY_INVALID_PATH("error.library.invalidPath"),
    NOT_LOGGED_IN("error.notLoggedIn"),
    GENERAL_ERROR("error.general"),
    LIBRARIES_UNINITIALISED("error.libraries.uninitialised"),
    PLAYLIST_NOT_FOUND("error.playlist.notFound"),
    EMAIL_INVALID("error.email.invalid"),
    TOKEN_INVALID("error.token.invalid"), 
    CONTAINS_MEDIA("error.containsMedia"),
    OUT_OF_STORAGE("error.outOfStorage");


    private String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}

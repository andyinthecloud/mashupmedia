package org.mashupmedia.dto.share;

public enum ErrorCode {

    NOT_UNIQUE("error.not-unique"),
    INCORRECT_PASSWORD("error.password.incorrect"),
    NON_MATCHING_PASSWORDS("error.password.matching"),
    LIBRARY_INVALID_PATH("error.library.invalid-path"),
    NOT_LOGGED_IN("error.not-logged-in"),
    GENERAL_ERROR("error.general"),
    LIBRARIES_UNINITIALISED("error.libraries.uninitialised"),
    PLAYLIST_NOT_FOUND("error.playlist.not-found"),
    EMAIL_INVALID("error.email.invalid"),
    TOKEN_INVALID("error.token.invalid"), 
    CONTAINS_MEDIA("error.contains.media");

    private String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}

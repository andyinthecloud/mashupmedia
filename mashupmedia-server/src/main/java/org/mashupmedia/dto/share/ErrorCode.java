package org.mashupmedia.dto.share;

public enum ErrorCode {

    NOT_UNIQUE("error.not-unique"),
    INCORRECT_PASSWORD("error.password.incorrect"),
    NON_MATCHING_PASSWORDS("error.password.matching"),
    LIBRARY_INVALID_PATH("error.library.invalid-path");
    


    private String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
    
}

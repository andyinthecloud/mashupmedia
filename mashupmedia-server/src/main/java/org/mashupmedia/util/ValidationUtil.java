package org.mashupmedia.util;

import org.mashupmedia.dto.share.ErrorPayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public class ValidationUtil {

    public static String DEFAULT_OK_RESPONSE_MESSAGE = "I am completely operational, and all my circuits are functioning perfectly.";
    public static String DEFAULT_ERROR_RESPONSE_MESSAGE = "I think you ought to know I'm feeling very depressed.";

    public static <T> ResponseEntity<ServerResponsePayload<T>> createResponseEntityPayload(T payload, Errors errors) {

        ErrorPayload errorPayload = null;
        if (errors.hasErrors()) {
            errorPayload = ErrorPayload.builder()
                    .fieldErrors(errors.getFieldErrors())
                    .objectErrors(errors.getGlobalErrors())
                    .build();
        }

        ServerResponsePayload<T> serverResponsePayload = ServerResponsePayload.<T>builder()
                .payload(payload)
                .errorPayload(errorPayload)
                .build();

        HttpStatus httpStatus = errors.hasErrors() ? HttpStatus.BAD_REQUEST : HttpStatus.OK;

        return new ResponseEntity<ServerResponsePayload<T>>(serverResponsePayload, httpStatus);

    }

}

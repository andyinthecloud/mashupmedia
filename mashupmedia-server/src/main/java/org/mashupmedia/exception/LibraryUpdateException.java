package org.mashupmedia.exception;

public class LibraryUpdateException extends MashupMediaRuntimeException {

    public LibraryUpdateException(String message, Throwable t) {
        super(message, t);
    }

    public LibraryUpdateException(String message) {
        super(message);
    }

}

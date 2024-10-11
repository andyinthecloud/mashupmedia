package org.mashupmedia.exception;

public class UserStorageException extends Exception{

    public UserStorageException() {
        super("User storage has no more space");
    }

    public UserStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

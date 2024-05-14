package org.mashupmedia.exception;

public class UserStorageException extends MashupMediaRuntimeException{

    public UserStorageException() {
        super("User storage has no more space");
    }

}

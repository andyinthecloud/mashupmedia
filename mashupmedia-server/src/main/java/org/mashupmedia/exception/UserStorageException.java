package org.mashupmedia.exception;

public class UserStorageException extends Exception{

    public UserStorageException() {
        super("User storage has no more space");
    }

}

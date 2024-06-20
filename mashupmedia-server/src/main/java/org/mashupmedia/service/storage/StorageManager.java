package org.mashupmedia.service.storage;

import java.nio.file.Path;

import org.mashupmedia.exception.UserStorageException;

public interface StorageManager {

    String store(Path path);

    void delete(Path path);

    void checkUserStorage(long size) throws UserStorageException;
    
}

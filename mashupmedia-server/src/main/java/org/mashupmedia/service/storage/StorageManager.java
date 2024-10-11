package org.mashupmedia.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.mashupmedia.exception.UserStorageException;

public interface StorageManager {

    String store(Path path);

    void delete(String resourceId);

    void checkUserStorage(long additionalSizeInBytes) throws UserStorageException;

    InputStream getInputStream(String resourceId) throws IOException;
    
}

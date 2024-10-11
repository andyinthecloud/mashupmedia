package org.mashupmedia.service.storage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.exception.UserStorageException;
import org.mashupmedia.model.account.Premium;
import org.mashupmedia.model.account.User;
import org.mashupmedia.util.AdminHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "mashupmedia.storage", havingValue = "file")
@Slf4j
public class FileStorageManagerImpl implements StorageManager {

    @Override
    public String store(Path path) {
        User user = AdminHelper.getLoggedInUser();
        Path storePath = user.createUploadResourcePath();

        try {
            Files.createFile(storePath);
            Files.copy(path, storePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Error copying file", e);
        }

        return storePath.normalize().toString();
    }

    @Override
    public void delete(String resourceId) {
        Path path = Path.of(resourceId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Unable to delete file", e);
        }
    }

    @Override
    public void checkUserStorage(long additionalSizeInBytes) throws UserStorageException {
        User user = AdminHelper.getLoggedInUser();
        Premium premium = user.getPremium();
        long userFolderSizeInBytes = FileUtils.sizeOfDirectory(user.getUserUploadPath().toFile());

        if ((additionalSizeInBytes + userFolderSizeInBytes) > premium.getSizeInBytes()) {
            throw new UserStorageException();
        }
        
    }

    @Override
    public InputStream getInputStream(String resourceId) throws IOException {
        Path path = Path.of(resourceId);        
        return new BufferedInputStream(Files.newInputStream(path));
    }

}

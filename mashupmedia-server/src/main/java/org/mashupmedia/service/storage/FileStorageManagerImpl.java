package org.mashupmedia.service.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.ehcache.shadow.org.terracotta.utilities.io.Files;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "mashupmedia.storage", havingValue = "file")
public class FileStorageManagerImpl implements StorageManager {@Override

    public String store(Path path) {
        User user = AdminHelper.getLoggedInUser();
        Path storePath = FileHelper.getUserUploadPath(user.getFolderName()).resolve(String.valueOf(System.currentTimeMillis()));        

        try {
            Files.createFile(storePath);
            Files.copy(path, storePath, StandardCopyOption.REPLACE_EXISTING);                
            Files.delete(path);
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Unable to delete temp file", e);
        }

        return storePath.normalize().toString();
    }


  


}

package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalStorageManagerImpl implements StorageManager {

    private final LibraryManager libraryManager;

    @Override
    public void store(MultipartFile multipartFile) {
        User user = AdminHelper.getLoggedInUser();
        File userLibraryFolder = FileHelper.getUserLibraryFolder(user.getLibraryFolderName());
        log.info("MM user libray folder: " + userLibraryFolder.getAbsolutePath());

        log.info("Saving uploaded file: " + multipartFile.getOriginalFilename());
    }

    @Override
    public List<File> getFiles(long libraryId, String folderPath) {
        Library library = libraryManager.getLibrary(libraryId);
        checkFolderAccessRights(library, folderPath);

        if (StringUtils.isEmpty(folderPath)) {
            File file = new File(library.getPath());
            File[] files = file.listFiles();
            Arrays.sort(files);
            return Arrays.asList(files);
        }

        File file = new File(folderPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Arrays.sort(files);
            return Arrays.asList(files);
        }

        throw new MashupMediaRuntimeException("Unable to get files for path: '" + folderPath);
    }

    private void checkFolderAccessRights(Library library, String folderPath) {
        User user = AdminHelper.getLoggedInUser();
        if (user.isAdministrator()) {
            return;
        }

        String libraryPath = library.getPath();
        if (!folderPath.startsWith(libraryPath)) {
            throw new IllegalAccessError("Only library files are accessible");
        }

        if (library.getUser().equals(user)) {
            return;
        }

        throw new IllegalAccessError(
                "User: " + user.getUsername() + " does not have rights to access library id: " + library.getId());
    }

    @Override
    public boolean rename(long libraryId, String path, String name) {
        Library library = libraryManager.getLibrary(libraryId);
        checkFolderAccessRights(library, path);
        File file = new File(path);
        File renameFile = new File(file.getParent(), name);
        return file.renameTo(renameFile);
    }

    @Override
    public boolean delete(long libraryId, String path) {
        Library library = libraryManager.getLibrary(libraryId);
        checkFolderAccessRights(library, path);
        File file = new File(path);
        if (file.isDirectory()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                throw new MashupMediaRuntimeException("Unable to delete path: " + path, e);
            }
            return true;
        } else {
            return file.delete();
        }
    }

}

package org.mashupmedia.service;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface StorageManager {

    void store(MultipartFile multipartFile);

    List<File> getFiles(long libraryId, String folderPath);

    boolean rename(long libraryId, String path, String name);

    boolean delete(long libraryId, String path);

}

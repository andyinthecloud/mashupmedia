package org.mashupmedia.service;

import java.io.File;
import java.util.Date;

import org.mashupmedia.model.library.PhotoLibrary;

public interface PhotoLibraryUpdateManager {

	public void updateLibrary(PhotoLibrary library, File folder, Date date);

	public void deleteObsoletePhotos(long libraryId, Date date);

	public void saveFile(PhotoLibrary library, File file, Date date);

	public void deleteFile(PhotoLibrary library, File file);

}

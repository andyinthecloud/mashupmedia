package org.mashupmedia.service;

import java.io.File;
import java.util.Date;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.VideoLibrary;

public interface VideoLibraryUpdateManager {

	public void updateLibrary(VideoLibrary library, File folder, Date date);

	public void deleteObsoleteVideos(Library library, Date date);

}

package org.mashupmedia.service;

import java.io.File;
import java.util.Date;

import org.mashupmedia.model.library.VideoLibrary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoLibraryUpdateManagerImpl implements VideoLibraryUpdateManager{

	@Override
	public void updateLibrary(VideoLibrary library, File folder, Date date) {
		String deriveTitle = library.getVideoDeriveTitle();
		
		//USE_FOLDER_NAME USE_FILE_NAME
		
		String folderName = folder.getName();
		
//		Video vi
		
		
		
	}

}

package org.mashupmedia.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.Song;

public interface MusicLibraryUpdateManager {

	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs, Date date);

	public void deleteSongs(List<Song> songs);

	public void deleteEmpty();

	public void updateRemoteLibrary(MusicLibrary musicLibrary) throws Exception;

	public void updateLibrary(MusicLibrary library, File folder, Date date);

	public void deleteObsoleteSongs(long libraryId, Date date);

	public void saveFile(MusicLibrary library, File file, Date date);

	public void deleteFile(MusicLibrary library, File file);

}

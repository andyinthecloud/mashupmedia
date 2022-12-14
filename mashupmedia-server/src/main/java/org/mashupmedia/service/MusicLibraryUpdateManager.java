package org.mashupmedia.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.Track;

public interface MusicLibraryUpdateManager {

	public void saveTracks(MusicLibrary musicLibrary, List<Track> tracks, Date date);

	public void deleteTracks(List<Track> tracks);

	public void deleteEmpty();

	public void updateRemoteLibrary(MusicLibrary musicLibrary) throws Exception;

	public void updateLibrary(MusicLibrary library, File folder, Date date);

	public void deleteObsoleteTracks(long libraryId, Date date);

	public void saveFile(MusicLibrary library, File file, Date date);

	public void deleteFile(MusicLibrary library, File file);

}

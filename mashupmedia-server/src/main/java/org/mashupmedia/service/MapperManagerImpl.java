/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sun.xml.txw2.annotation.XmlNamespace;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.xml.PartialUnmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MapperManagerImpl implements MapperManager {

	private Marshaller marshaller;

	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	protected Marshaller getMarshaller() throws JAXBException {
		if (marshaller != null) {
			return marshaller;
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(Song.class);
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
		return marshaller;
	}

	@Override
	public void writeStartRemoteMusicLibraryXml(long libraryId, LibraryType libraryType) throws Exception {
		File file = FileHelper.getLibraryXmlFile(libraryId);
		FileWriter writer = new FileWriter(file, false);
		writer.write("<?xml version=\"1.0\" ?>");
		
		writer.write("<library type=\""+libraryType.name().toLowerCase()+"\">");
		writer.close();
	}

	@Override
	public void writeEndRemoteMusicLibraryXml(long libraryId) throws Exception {
		File file = FileHelper.getLibraryXmlFile(libraryId);
		FileWriter writer = new FileWriter(file, true);
		writer.write("</library>");
		writer.close();
	}

	@Override
	public void writeSongToXml(long libraryId, Song song) throws Exception {
		if (song == null) {
			return;
		}

		Song clonedSong = SerializationUtils.clone(song);
		clonedSong.setId(0);
		clonedSong.setPath(String.valueOf(song.getId()));

		String fileName = clonedSong.getFileName();
		clonedSong.setFileName(StringHelper.escapeXml(fileName));

		String songTitle = clonedSong.getTitle();
		clonedSong.setTitle(StringHelper.escapeXml(songTitle));

		String songSearchText = clonedSong.getSearchText();
		clonedSong.setSearchText(StringHelper.escapeXml(songSearchText));

		String summary = clonedSong.getSummary();
		clonedSong.setSummary(StringHelper.escapeXml(summary));

		String displayTitle = clonedSong.getDisplayTitle();
		clonedSong.setDisplayTitle(StringHelper.escapeXml(displayTitle));

		Artist clonedArtist = SerializationUtils.clone(song.getArtist());
		String artistName = clonedArtist.getName();
		clonedArtist.setName(StringHelper.escapeXml(artistName));
		String artistIndexText = clonedArtist.getIndexText();
		clonedArtist.setIndexText(StringHelper.escapeXml(artistIndexText));
		clonedSong.setArtist(clonedArtist);

		Album clonedAlbum = SerializationUtils.clone(song.getAlbum());
		String albumName = clonedAlbum.getName();
		clonedAlbum.setName(StringHelper.escapeXml(albumName));
		String albumFolderName = clonedAlbum.getFolderName();
		clonedAlbum.setFolderName(StringHelper.escapeXml(albumFolderName));
		clonedSong.setAlbum(clonedAlbum);

		File file = FileHelper.getLibraryXmlFile(libraryId);

		FileWriter writer = new FileWriter(file, true);
		
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.writeValue(writer, clonedSong);
		writer.close();
	}

	@Override
	public void saveXmltoSongs(MusicLibrary musicLibrary, String xml) throws Exception {

		String libraryPath = StringUtils.trimToEmpty(musicLibrary.getLocation().getPath());
		libraryPath = libraryPath.replaceFirst("/.*", "");

		List<Song> songs = new ArrayList<Song>();

		if (StringUtils.isBlank(xml)) {
			log.error("Unable to save remote songs, xml is empty");
			return;
		}

		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

		PartialUnmarshaller<Song> partialUnmarshaller = new PartialUnmarshaller<Song>(inputStream, Song.class);

		Date date = new Date();		
		while (partialUnmarshaller.hasNext()) {
			Song song = partialUnmarshaller.next();
			String title = StringEscapeUtils.unescapeXml(song.getTitle());
			song.setTitle(title);
			Album album = song.getAlbum();
			album.setId(0);

			songs.add(song);

			if (songs.size() == 10) {
				musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);
				songs.clear();
			}
		}

		partialUnmarshaller.close();
		musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);

	}

}

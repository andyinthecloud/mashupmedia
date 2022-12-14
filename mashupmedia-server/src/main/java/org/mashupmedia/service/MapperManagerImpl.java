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

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.xml.PartialUnmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MapperManagerImpl implements MapperManager {

	private Marshaller marshaller;

	@Autowired
	@Lazy
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	@Autowired
	private ObjectMapper objectMapper;

	protected Marshaller getMarshaller() throws JAXBException {
		if (marshaller != null) {
			return marshaller;
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(Track.class);
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
		return marshaller;
	}

	@Override
	public void writeStartRemoteMusicLibraryXml(long libraryId, LibraryType libraryType) throws Exception {
		File file = FileHelper.getLibraryXmlFile(libraryId);
		FileWriter writer = new FileWriter(file, false);
		writer.write("<?xml version=\"1.0\" ?>");

		writer.write("<library type=\"" + libraryType.name().toLowerCase() + "\">");
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
	public void writeTrackToXml(long libraryId, Track track) throws Exception {
		if (track == null) {
			return;
		}

		Track clonedTrack = SerializationUtils.clone(track);
		clonedTrack.setId(0);
		clonedTrack.setPath(String.valueOf(track.getId()));

		String fileName = clonedTrack.getFileName();
		clonedTrack.setFileName(StringHelper.escapeXml(fileName));

		String trackTitle = clonedTrack.getTitle();
		clonedTrack.setTitle(StringHelper.escapeXml(trackTitle));

		String trackSearchText = clonedTrack.getSearchText();
		clonedTrack.setSearchText(StringHelper.escapeXml(trackSearchText));

		String summary = clonedTrack.getSummary();
		clonedTrack.setSummary(StringHelper.escapeXml(summary));

		String displayTitle = clonedTrack.getDisplayTitle();
		clonedTrack.setDisplayTitle(StringHelper.escapeXml(displayTitle));

		Artist clonedArtist = SerializationUtils.clone(track.getArtist());
		String artistName = clonedArtist.getName();
		clonedArtist.setName(StringHelper.escapeXml(artistName));
		String artistIndexText = clonedArtist.getIndexText();
		clonedArtist.setIndexText(StringHelper.escapeXml(artistIndexText));
		clonedTrack.setArtist(clonedArtist);

		Album clonedAlbum = SerializationUtils.clone(track.getAlbum());
		String albumName = clonedAlbum.getName();
		clonedAlbum.setName(StringHelper.escapeXml(albumName));
		String albumFolderName = clonedAlbum.getFolderName();
		clonedAlbum.setFolderName(StringHelper.escapeXml(albumFolderName));
		clonedTrack.setAlbum(clonedAlbum);
		File file = FileHelper.getLibraryXmlFile(libraryId);
		objectMapper.writeValue(file, clonedTrack);
	}

	@Override
	public void saveXmltoTracks(MusicLibrary musicLibrary, String xml) throws Exception {

		String libraryPath = StringUtils.trimToEmpty(musicLibrary.getLocation().getPath());
		libraryPath = libraryPath.replaceFirst("/.*", "");

		List<Track> tracks = new ArrayList<Track>();

		if (StringUtils.isBlank(xml)) {
			log.error("Unable to save remote tracks, xml is empty");
			return;
		}

		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

		PartialUnmarshaller<Track> partialUnmarshaller = new PartialUnmarshaller<Track>(inputStream, Track.class);

		Date date = new Date();
		while (partialUnmarshaller.hasNext()) {
			Track track = partialUnmarshaller.next();
			String title = StringEscapeUtils.unescapeXml(track.getTitle());
			track.setTitle(title);
			Album album = track.getAlbum();
			album.setId(0);

			tracks.add(track);

			if (tracks.size() == 10) {
				musicLibraryUpdateManager.saveTracks(musicLibrary, tracks, date);
				tracks.clear();
			}
		}

		partialUnmarshaller.close();
		musicLibraryUpdateManager.saveTracks(musicLibrary, tracks, date);

	}

}

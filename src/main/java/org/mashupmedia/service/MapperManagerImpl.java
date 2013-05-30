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

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.SerializationUtils;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.util.XmlHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MapperManagerImpl implements MapperManager {

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	protected Marshaller getMarshaller() throws JAXBException {
		if (marshaller != null) {
			return marshaller;
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(Song.class);
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
		return marshaller;
	}

	protected Unmarshaller getUnmarshaller() throws JAXBException {
		if (unmarshaller != null) {
			return unmarshaller;
		}

		unmarshaller = JAXBContext.newInstance(Song.class).createUnmarshaller();
		return unmarshaller;
	}

	@Override
	public void writeStartRemoteMusicLibraryXml(long libraryId) throws Exception {
		File file = FileHelper.getLibraryXmlFile(libraryId);
		FileWriter writer = new FileWriter(file, false);
		writer.write("<?xml version=\"1.0\" ?>");
		writer.write("<library>");
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
		getMarshaller().marshal(clonedSong, new StreamResult(writer));
		writer.close();
	}

	@Override
	public List<Song> convertXmltoSongs(String xml) throws Exception {

		List<Song> songs = new ArrayList<Song>();

		StringReader reader = new StringReader(xml);
		XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
		XmlHelper.skipElements(xmlReader, XMLStreamReader.START_DOCUMENT, XMLStreamReader.DTD);
		xmlReader.nextTag();
		while (xmlReader.hasNext()) {
			Song song = (Song) getUnmarshaller().unmarshal(xmlReader);
			songs.add(song);

			if (xmlReader.nextTag() != XMLStreamReader.START_ELEMENT) {
				break;
			}

		}

		reader.close();
		xmlReader.close();
		return songs;
	}

}

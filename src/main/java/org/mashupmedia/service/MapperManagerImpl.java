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
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.SerializationUtils;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.remote.RemoteMusicLibrary;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MapperManagerImpl implements MapperManager {

	javax.xml.bind.Marshaller jaxbMarshaller;
	
	@Autowired
	private Marshaller marshaller;

	@Autowired
	private Unmarshaller unmarshaller;
	
//	private Marshaller marshaller;
//	
//	protected Marshaller getPartialMarshaller() throws JAXBException {
//		if (marshaller != null) {
//			return marshaller;
//		}
//		
//		JAXBContext jaxbContext = JAXBContext.newInstance(RemoteMusicLibrary.class, Song.class);		
//		javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
//		this.marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
//		return marshaller;
//	}
	
	
	

	@Override
	public void writeStartRemoteMusicLibraryXml(long libraryId) throws Exception {
		File file = FileHelper.getLibraryXmlFile(libraryId);
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		FileWriter writer = new  FileWriter(file, false);		
		XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);
		streamWriter.writeStartDocument();
		String startElement = StringHelper.formatFirstLetterToLowercase(RemoteMusicLibrary.class.getSimpleName());
		streamWriter.writeStartElement(startElement);
		writer.close();
	}
	
	@Override
	public void writeEndRemoteMusicLibraryXml(long libraryId) throws Exception {
		File file = FileHelper.getLibraryXmlFile(libraryId);
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		FileWriter writer = new  FileWriter(file, true);		
		XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);
		streamWriter.writeEndElement();
		streamWriter.writeEndDocument();		
		writer.close();
	}
	
	@Override
	public void writeSongToXml(long libraryId, Song song) throws Exception {
		if (song == null) {
			return;
		}

//		List<Song> clonedSongs = new ArrayList<Song>();

		
		
		
//		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		
//        XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(new FileOutputStream(file));
        
//        Marshaller marshaller = getPartialMarshaller();

		

			Song clonedSong = SerializationUtils.clone(song);
			clonedSong.setId(0);
			clonedSong.setPath(String.valueOf(song.getId()));

			String fileName = clonedSong.getFileName();
			clonedSong.setFileName(StringHelper.escapeXml(fileName));

			// String path = clonedSong.getPath();
			// clonedSong.setPath(StringHelper.escapeXml(path));

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
			marshaller.marshal(clonedSong, new StreamResult(writer));
			
			
//			FileWriter writer = new  FileWriter(file, true);
			
//			IOUtils.w

//			clonedSongs.add(clonedSong);
//		}
		
			
			
		writer.close();

//		RemoteMusicLibrary remoteMusicLibrary = new RemoteMusicLibrary();
//		remoteMusicLibrary.setSongs(clonedSongs);

//		ByteArrayOutputStream outputStream = null;
//		try {
//			outputStream = new ByteArrayOutputStream();
//			marshaller.marshal(remoteMusicLibrary, new StreamResult(outputStream));
//			
//			
//			String xml = outputStream.toString(Encoding.UTF8.getEncodingString());
//			File file = FileHelper.getLibraryXmlFile(libraryId);
//			FileUtils.writeStringToFile(file, xml);
//		} finally {
//			outputStream.close();
//		}
		
        
		
		
		
	}

	
	
	@Override
	public RemoteMusicLibrary convertXmltoRemoteMusicLibrary(String xml) throws Exception {
		return null;
//		StringReader stringReader = new StringReader(xml);
//		RemoteMusicLibrary remoteMusicLibrary = (RemoteMusicLibrary) unmarshaller.unmarshal(new StreamSource(stringReader));
//		List<Song> songs = remoteMusicLibrary.getSongs();
//		for (Song song : songs) {
//			Album album = song.getAlbum();
//			Artist artist = song.getArtist();
//			album.setArtist(artist);
//			song.setAlbum(album);
//		}
//
//		return remoteMusicLibrary;
	}

}

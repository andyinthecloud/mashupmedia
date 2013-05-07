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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.remote.RemoteMusicLibrary;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.util.StringHelper.Encoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MapperManagerImpl implements MapperManager {

	@Autowired
	private Marshaller marshaller;

	@Autowired
	private Unmarshaller unmarshaller;

	@Override
	public void convertSongsToXml(long libraryId, List<Song> songs) throws Exception {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		List<Song> clonedSongs = new ArrayList<Song>();
		
		for (Song song : songs) {
			
			Song clonedSong = SerializationUtils.clone(song);
			
			String fileName = clonedSong.getFileName();
			clonedSong.setFileName(StringHelper.escapeXml(fileName));

			String path = clonedSong.getPath();
			clonedSong.setPath(StringHelper.escapeXml(path));

			String songTitle = clonedSong.getTitle();
			clonedSong.setTitle(StringHelper.escapeXml(songTitle));
			
			String songSearchText = clonedSong.getSearchText();
			clonedSong.setSearchText(StringHelper.escapeXml(songSearchText));

			String summary = clonedSong.getSummary();
			clonedSong.setSummary(StringHelper.escapeXml(summary));

			String displayTitle = clonedSong.getDisplayTitle();
			clonedSong.setDisplayTitle(StringHelper.escapeXml(displayTitle));

			Artist clonedArtist =  SerializationUtils.clone(song.getArtist());
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
			
			clonedSongs.add(clonedSong);
		}

		RemoteMusicLibrary remoteMusicLibrary = new RemoteMusicLibrary();
		remoteMusicLibrary.setSongs(clonedSongs);

		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			marshaller.marshal(remoteMusicLibrary, new StreamResult(outputStream));
			String xml = outputStream.toString(Encoding.UTF8.getEncodingString());
			File file = FileHelper.getLibraryXmlFile(libraryId);
			FileUtils.writeStringToFile(file, xml);
		} finally {
			outputStream.close();
		}
	}

	@Override
	public RemoteMusicLibrary convertXmltoRemoteMusicLibrary(String xml) throws Exception {
		StringReader stringReader = new StringReader(xml);
//		StreamSource streamSource = new StreamSource(stringReader);
//		RemoteMusicLibrary remoteMusicLibrary = null;
//		try {
			RemoteMusicLibrary remoteMusicLibrary = (RemoteMusicLibrary) unmarshaller.unmarshal(new StreamSource(stringReader));
//		} finally {
//			stringReader.close();
//		}
		return remoteMusicLibrary;
	}

}

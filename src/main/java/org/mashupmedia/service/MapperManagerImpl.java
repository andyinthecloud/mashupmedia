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
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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

	@Autowired
	private Marshaller marshaller;

	@Autowired
	private Unmarshaller unmarshaller;

	@Override
	public void convertSongsToXml(long libraryId, List<Song> songs) throws Exception {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		for (Song song : songs) {
			String fileName = song.getFileName();
			song.setFileName(StringHelper.escapeXml(fileName));

			String path = song.getPath();
			song.setPath(StringHelper.escapeXml(path));

			String songTitle = song.getTitle();
			song.setTitle(StringHelper.escapeXml(songTitle));

			String songSearchText = song.getSearchText();
			song.setSearchText(StringHelper.escapeXml(songSearchText));

			String summary = song.getSummary();
			song.setSummary(StringHelper.escapeXml(summary));

			String displayTitle = song.getDisplayTitle();
			song.setDisplayTitle(StringHelper.escapeXml(displayTitle));

			Artist artist = song.getArtist();
			String artistName = artist.getName();
			artist.setName(StringHelper.escapeXml(artistName));

			Album album = song.getAlbum();
			String albumName = album.getName();
			album.setName(StringHelper.escapeXml(albumName));
			String albumFolderName = album.getFolderName();
			album.setFolderName(StringHelper.escapeXml(albumFolderName));
		}

		RemoteMusicLibrary remoteMusicLibrary = new RemoteMusicLibrary();
		remoteMusicLibrary.setSongs(songs);

		File file = FileHelper.getLibraryXmlFile(libraryId);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			marshaller.marshal(remoteMusicLibrary, new StreamResult(fileOutputStream));
			fileOutputStream.flush();
		} finally {
			fileOutputStream.close();

		}

		// StringWriter writer = new StringWriter();

		// marshaller.

		// Marshaller marshaller = new Marshaller(writer);
		// marshaller.setEncoding(Encoding.UTF8.getEncodingString());
		// marshaller.setProperty("jaxb.encoding", "Unicode");
		// marshaller.marshal(remoteMusicLibrary);
		//
		// FileUtils.writeStringToFile(file, writer.toString(),
		// Encoding.UTF8.getEncodingString());
	}

	@Override
	public RemoteMusicLibrary convertXmltoRemoteMusicLibrary(String xml) throws Exception {
		StringReader stringReader = new StringReader(xml);
		StreamSource streamSource = new StreamSource(stringReader);
		RemoteMusicLibrary remoteMusicLibrary = null;
		try {
			remoteMusicLibrary = (RemoteMusicLibrary) unmarshaller.unmarshal(streamSource);
		} finally {
			stringReader.close();
		}
		return remoteMusicLibrary;

	}

}

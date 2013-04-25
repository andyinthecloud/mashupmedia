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

package org.mashupmedia.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.remote.RemoteMusicLibrary;
import org.mashupmedia.util.StringHelper.Encoding;

public class MediaItemHelper {

	public static MediaType getMediaType(String mediaTypeValue) {
		if (mediaTypeValue == null) {
			return null;
		}

		MediaType[] mediaTypes = MediaType.values();
		for (MediaType mediaType : mediaTypes) {
			if (mediaTypeValue.equalsIgnoreCase(mediaType.toString())) {
				return mediaType;
			}
		}

		return null;
	}

	public static boolean isEquals(MediaItem mediaItem1, MediaItem mediaItem2) {
		if (mediaItem1 == null || mediaItem2 == null) {
			return false;
		}

		if (mediaItem1.getId() == mediaItem2.getId()) {
			return true;
		}

		return false;
	}

	public static MediaSortType getMediaSortType(String mediaSortTypeValue) {
		mediaSortTypeValue = StringUtils.trimToEmpty(mediaSortTypeValue);
		if (StringUtils.isEmpty(mediaSortTypeValue)) {
			return MediaSortType.SONG_TITLE;
		}
		
		MediaSortType[] mediaSortTypes = MediaSortType.values();
		for (MediaSortType mediaSortType : mediaSortTypes) {
			if (mediaSortType.toString().equalsIgnoreCase(mediaSortTypeValue)) {
				return mediaSortType;
			}
		}
		
		return MediaSortType.SONG_TITLE;
	}
	
	public static void convertSongsToXml(long libraryId, List<Song> songs) throws IOException, MarshalException, ValidationException {
		if (songs == null || songs.isEmpty()) {
			return;
		}
				
		RemoteMusicLibrary remoteMusicLibrary = new RemoteMusicLibrary();
		remoteMusicLibrary.setSongs(songs);
		
		File file = FileHelper.getLibraryXmlFile(libraryId);
		
		StringWriter writer = new StringWriter();				
		Marshaller marshaller = new Marshaller(writer);
		marshaller.setEncoding(Encoding.UTF8.getEncodingString());
		marshaller.marshal(remoteMusicLibrary);
				
		FileUtils.writeStringToFile(file, writer.toString(), Encoding.UTF8.getEncodingString());		
	}
	
	public static RemoteMusicLibrary convertXmltoRemoteMusicLibrary(String xml) throws MarshalException, ValidationException {		
		Unmarshaller unmarshaller = new Unmarshaller(RemoteMusicLibrary.class);
		StringReader stringReader = new StringReader(xml);
		RemoteMusicLibrary remoteMusicLibrary = (RemoteMusicLibrary) unmarshaller.unmarshal(stringReader);
		return remoteMusicLibrary;
	}

}

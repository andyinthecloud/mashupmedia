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

import java.util.List;

import org.mashupmedia.model.media.Song;
import org.mashupmedia.remote.RemoteMusicLibrary;

public interface MapperManager {

	public void convertSongsToXml(long libraryId, List<Song> songs) throws Exception;
	
	public RemoteMusicLibrary convertXmltoRemoteMusicLibrary(String xml) throws Exception;
	
	
}

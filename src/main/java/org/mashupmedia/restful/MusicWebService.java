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

package org.mashupmedia.restful;

import java.util.List;

import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public interface MusicWebService {
	public final static int IMAGE_MAX_WIDTH = 500;
	public final static int MAX_IMAGES = 20;

	public RemoteMediaMetaItem getArtistInformation(Artist artist) throws Exception;

	public List<RemoteMediaMetaItem> searchArtist(String artistName) throws Exception;
}

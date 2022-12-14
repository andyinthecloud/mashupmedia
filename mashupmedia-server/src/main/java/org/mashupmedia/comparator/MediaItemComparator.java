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

package org.mashupmedia.comparator;

import java.util.Comparator;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.music.Track;

public class MediaItemComparator implements Comparator<MediaItem>{

	@Override
	public int compare(MediaItem o1, MediaItem o2) {
		MediaType mediaType =  o1.getMediaType();
		
		int compare = 0;
		if (mediaType == MediaType.TRACK) {
			Track track1 = (Track) o1;
			Track track2 = (Track) o2;
			compare = compare(track1, track2);
		}		
		
		return compare;
	}
	
	public int compare(Track o1, Track o2) {
		String o1DisplayTitle = o1.getDisplayTitle();
		String o2DisplayTitle = o2.getDisplayTitle();
		int compare = o1DisplayTitle.compareToIgnoreCase(o2DisplayTitle);
		return compare;
	}
	
	
	
	

}

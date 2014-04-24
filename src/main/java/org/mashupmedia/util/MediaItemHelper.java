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

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;

public class MediaItemHelper {

	public enum MediaContentType {
		MP3("audio/mpeg", "mp3", "mp3", 1), M4A("audio/m4a", "m4a", "m4a", 2), OGA("audio/ogg", "oga", "oga", 3), WAV(
				"audio/wav", "wav", "wav", 3), FLAC("audio/flac", "flac", "flac", 4), UNSUPPORTED("media/unsupported",
				"unsupported", "unsupported", 100), MP4("video/mp4", "m4v", "mp4", 1), WEBM("video/webm", "webmv",
				"webm", 2), OGV("video/ogg", "ogv", "ogv", 3);

		private String mimeContentType;
		private String jPlayerContentType;
		private String name;
		private int ranking;

		private MediaContentType(String mimeContentType, String jPlayerContentType, String name, int ranking) {
			this.mimeContentType = mimeContentType;
			this.jPlayerContentType = jPlayerContentType;
			this.name = name;
			this.ranking = ranking;
		}

		public String getjPlayerContentType() {
			return jPlayerContentType;
		}

		public String getMimeContentType() {
			return mimeContentType;
		}

		public String getName() {
			return name;
		}

		public int getRanking() {
			return ranking;
		}

	}

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

	public static MediaContentType getMediaContentType(String format) {

		format = StringUtils.trimToEmpty(format);

		if (format.equalsIgnoreCase("MPEG-1 Layer 3") || format.equalsIgnoreCase("mp3")) {
			return MediaContentType.MP3;
		} else if (format.equalsIgnoreCase("Vorbis") || format.equalsIgnoreCase("ogg")
				|| format.equalsIgnoreCase("oga")) {
			return MediaContentType.OGA;
		} else if (format.equalsIgnoreCase("Free Lossless Audio Codec") || format.equalsIgnoreCase("flac")) {
			return MediaContentType.FLAC;
		} else if (format.equalsIgnoreCase("webm")) {
			return MediaContentType.WEBM;
		} else if (format.equalsIgnoreCase("mp4") || format.equalsIgnoreCase("m4v")) {
			return MediaContentType.MP4;
		} else if (format.equalsIgnoreCase("ogv")) {
			return MediaContentType.OGV;
		}

		return MediaContentType.UNSUPPORTED;
	}

	public static boolean isCompatibleVideoFormat(MediaContentType mediaContentType) {
		if (mediaContentType == MediaContentType.MP4) {
			return true;
		}

		if (mediaContentType == MediaContentType.WEBM) {
			return true;
		}

		if (mediaContentType == MediaContentType.OGV) {
			return true;
		}

		return false;
	}

}

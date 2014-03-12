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
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;

public class MediaItemHelper {

	public enum MediaContentType {
		MP3_ENCODED("audio/mpeg", "mp3", "mp3"), MP3_ORIGINAL("audio/mpeg", "mp3", "mp3"), OGA("audio/ogg", "oga",
				"oga"), FLAC("audio/flac", "flac", "flac"), UNSUPPORTED("media/unsupported", "unsupported",
				"unsupported"), WEBM("video/webm", "webmv", "webm"), MP4("video/mp4", "m4v", "mp4");

		private String mimeContentType;
		private String jPlayerContentType;
		private String name;

		private MediaContentType(String mimeContentType, String jPlayerContentType, String name) {
			this.mimeContentType = mimeContentType;
			this.jPlayerContentType = jPlayerContentType;
			this.name = name;
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

	public static MediaContentType getEncodedMediaContentType(String mediaFormat) {
		mediaFormat = StringUtils.trimToEmpty(mediaFormat);
		if (StringUtils.isEmpty(mediaFormat)) {
			return MediaContentType.UNSUPPORTED;
		}

		if (mediaFormat.equalsIgnoreCase("MPEG-1 Layer 3") || mediaFormat.equalsIgnoreCase("mp3")) {
			return MediaContentType.MP3_ENCODED;
		}

		MediaContentType mediaContentType = getMediaContentType(mediaFormat);
		return mediaContentType;

	}

	public static MediaContentType getOriginalMediaContentType(String mediaFormat) {
		mediaFormat = StringUtils.trimToEmpty(mediaFormat);
		if (StringUtils.isEmpty(mediaFormat)) {
			return MediaContentType.UNSUPPORTED;
		}

		if (mediaFormat.equalsIgnoreCase("MPEG-1 Layer 3") || mediaFormat.equalsIgnoreCase("mp3")) {
			return MediaContentType.MP3_ORIGINAL;
		}

		MediaContentType mediaContentType = getMediaContentType(mediaFormat);
		return mediaContentType;

	}

	private static MediaContentType getMediaContentType(String mediaFormat) {
		if (mediaFormat.equalsIgnoreCase("Vorbis") || mediaFormat.equalsIgnoreCase("ogg")) {
			return MediaContentType.OGA;
		} else if (mediaFormat.equalsIgnoreCase("Free Lossless Audio Codec") || mediaFormat.equalsIgnoreCase("flac")) {
			return MediaContentType.FLAC;
		} else if (mediaFormat.equalsIgnoreCase("webm")) {
			return MediaContentType.WEBM;
		} else if (mediaFormat.equalsIgnoreCase("mp4")) {
			return MediaContentType.MP4;
		}

		return MediaContentType.UNSUPPORTED;

	}

	public static MediaContentType getMediaContentType(MediaItem mediaItem) {
		MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
		if (mediaEncoding != null) {
			return mediaEncoding.getMediaContentType();
		}

		String format = mediaItem.getFormat();
		MediaContentType mediaContentType = getOriginalMediaContentType(format);
		if (mediaContentType == MediaContentType.MP3_ENCODED) {
			mediaContentType = MediaContentType.MP3_ORIGINAL;
		}

		if (mediaContentType != MediaContentType.UNSUPPORTED) {
			return mediaContentType;
		}

		// MediaType mediaType = mediaItem.getMediaType();
		// if (mediaType == MediaType.SONG) {
		// return MediaContentType.MP3;
		// }
		//
		// if (mediaType == MediaType.VIDEO) {
		// return MediaContentType.WEBM;
		// }

		return MediaContentType.UNSUPPORTED;
	}

}

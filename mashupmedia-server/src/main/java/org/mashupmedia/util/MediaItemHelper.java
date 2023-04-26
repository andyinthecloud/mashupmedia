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

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MashupMediaType;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.media.video.Video;

public class MediaItemHelper {

	public enum MediaContentType {
		AUDIO_MP3("audio/mpeg", 1),
		VIDEO_MP4("video/mp4", 1),
		VIDEO_WEBM("video/webm", 2),
		VIDEO_OGG("video/ogg", 3),
		VIDEO_WMV("video/x-ms-wmv", 4),
		IMAGE_JPG("image/jpeg", 1),
		IMAGE_PNG("image/png", 2),
		IMAGE_GIF("image/gif", 3),
		IMAGE_TIFF("image/tiff", 4),
		MEDIA_UNSUPPORTED("media/unsupported", 100);

		private final String contentType;
		private final int ranking;

		private MediaContentType(String mimeContentType, int ranking) {
			this.contentType = mimeContentType;
			this.ranking = ranking;
		}

		public String getContentType() {
			return contentType;
		}

		public int getRanking() {
			return ranking;
		}
	}

	public enum MediaItemSequenceType {
		PHOTO_ALBUM, LATEST, ALPHABETICAL
	}

	public static MashupMediaType getMediaType(String mediaTypeValue) {
		if (mediaTypeValue == null) {
			return null;
		}

		MashupMediaType[] mediaTypes = MashupMediaType.values();
		for (MashupMediaType mediaType : mediaTypes) {
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
			return MediaSortType.TRACK_TITLE;
		}

		MediaSortType[] mediaSortTypes = MediaSortType.values();
		for (MediaSortType mediaSortType : mediaSortTypes) {
			if (mediaSortType.toString().equalsIgnoreCase(mediaSortTypeValue)) {
				return mediaSortType;
			}
		}

		return MediaSortType.TRACK_TITLE;
	}

	public static MediaEncoding createMediaEncoding(String fileName) {
		String fileExtension = FileHelper.getFileExtension(fileName);
		MediaEncoding mediaEncoding = new MediaEncoding();
		mediaEncoding.setOriginal(true);
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(fileExtension);
		mediaEncoding.setMediaContentType(mediaContentType);
		return mediaEncoding;
	}

	public static MediaContentType getMediaContentType(String format) {

		format = StringUtils.trimToEmpty(format);

		if (format.equalsIgnoreCase("MPEG-1 Layer 3") || format.equalsIgnoreCase("mp3")) {
			return MediaContentType.AUDIO_MP3;
		} else if (format.equalsIgnoreCase("webm")) {
			return MediaContentType.VIDEO_WEBM;
		} else if (format.equalsIgnoreCase("mp4") || format.equalsIgnoreCase("m4v")) {
			return MediaContentType.VIDEO_MP4;
		} else if (format.equalsIgnoreCase("ogv")) {
			return MediaContentType.VIDEO_OGG;
		} else if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
			return MediaContentType.IMAGE_JPG;
		} else if (format.equalsIgnoreCase("png")) {
			return MediaContentType.IMAGE_PNG;
		} else if (format.equalsIgnoreCase("gif")) {
			return MediaContentType.IMAGE_GIF;
		} else if (format.equalsIgnoreCase("tif") || format.equalsIgnoreCase("tiff")) {
			return MediaContentType.IMAGE_TIFF;
		}

		return MediaContentType.MEDIA_UNSUPPORTED;
	}

	public static MediaContentType getDefaultMediaContentType(MediaItem mediaItem) {
		if (mediaItem instanceof Track) {
			return MediaContentType.AUDIO_MP3;
		}

		return MediaContentType.MEDIA_UNSUPPORTED;

	}

	public static boolean isCompatibleVideoFormat(MediaContentType mediaContentType) {
		if (mediaContentType == MediaContentType.VIDEO_MP4) {
			return true;
		}

		if (mediaContentType == MediaContentType.VIDEO_WEBM) {
			return true;
		}

		if (mediaContentType == MediaContentType.VIDEO_OGG) {
			return true;
		}

		return false;
	}

	public static boolean isCompatiblePhotoFormat(MediaContentType mediaContentType) {
		if (mediaContentType == MediaContentType.IMAGE_JPG) {
			return true;
		}

		if (mediaContentType == MediaContentType.IMAGE_GIF) {
			return true;
		}

		if (mediaContentType == MediaContentType.IMAGE_TIFF) {
			return true;
		}

		if (mediaContentType == MediaContentType.IMAGE_PNG) {
			return true;
		}

		return false;
	}

	public static boolean hasMediaEncoding(MediaItem mediaItem, MediaContentType mediaContentType) {
		Set<MediaEncoding> mediaEncodings = mediaItem.getMediaEncodings();
		if (mediaEncodings == null || mediaEncodings.isEmpty()) {
			return false;
		}

		for (MediaEncoding mediaEncoding : mediaEncodings) {
			if (mediaContentType == mediaEncoding.getMediaContentType()) {
				return true;
			}
		}
		return false;
	}

	public static String prepareUrlStream(String contextPath, long mediaItemId, String format) {
		StringBuilder urlBuilder = new StringBuilder(contextPath);
		urlBuilder.append("/streaming/media/");
		urlBuilder.append(mediaItemId);
		urlBuilder.append("/");
		urlBuilder.append(format);

		return urlBuilder.toString();
	}

	public static long getMediaContentDuration(MediaItem mediaItem) {
		long duration = 0;
		if (mediaItem == null) {
			return duration;
		}

		if (mediaItem instanceof Track) {
			duration = ((Track) mediaItem).getTrackLength();
			return duration;
		}

		if (mediaItem instanceof Video) {
			// duration = ((Video) mediaItem.get);
		}

		return duration;
	}

	public static MediaItemSequenceType getSequenceType(String sequenceTypeValue) {
		sequenceTypeValue = StringUtils.trimToEmpty(sequenceTypeValue);
		if (StringUtils.isEmpty(sequenceTypeValue)) {
			return MediaItemSequenceType.LATEST;
		}

		MediaItemSequenceType[] mediaItemSequenceTypes = MediaItemSequenceType.values();
		for (MediaItemSequenceType mediaItemSequenceType : mediaItemSequenceTypes) {
			if (sequenceTypeValue.equalsIgnoreCase(mediaItemSequenceType.name())) {
				return mediaItemSequenceType;
			}
		}

		return MediaItemSequenceType.LATEST;
	}

	public static boolean isWebCompatibleEncoding(MashupMediaType mashupMediaType, MediaContentType mediaContentType) {
		if (mashupMediaType == MashupMediaType.TRACK) {
			return isFormat(mediaContentType, MediaContentType.AUDIO_MP3);
		}

		return false;
	}

	private static boolean isFormat(MediaContentType mediaContentType, MediaContentType... mediaContentTypeFormats) {
		for (MediaContentType mediaContentTypeFormat : mediaContentTypeFormats) {
			if (mediaContentTypeFormat == mediaContentType) {
				return true;
			}
		}

		return false;
	}

}

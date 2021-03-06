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

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.web.restful.RestfulStream;

public class MediaItemHelper {

	public enum MediaContentType {
		MP3("audio/mpeg", "mp3", "mp3", 1), M4A("audio/m4a", "m4a", "m4a", 2), OGA("audio/ogg", "oga", "oga", 3), WAV(
				"audio/wav", "wav", "wav", 3), FLAC("audio/flac", "flac", "flac", 4), WMA("audio/x-ms-wma", "wma",
						"wma", 5), UNSUPPORTED("media/unsupported", "unsupported", "unsupported", 100), MP4("video/mp4",
								"m4v", "mp4", 1), WEBM("video/webm", "webmv", "webm", 2), OGV("video/ogg", "ogv", "ogv",
										3), WMV("video/x-ms-wmv", "wmv", "wmv", 4), JPEG("image/jpeg", "jpg", "jpg",
												1), PNG("image/png", "png", "png", 2), GIF("image/gif", "gif", "gif",
														3), TIF("image/tiff", "tiff", "tiff", 4);

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
	
	
	public enum MediaItemSequenceType {
		PHOTO_ALBUM, LATEST, ALPHABETICAL
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
		} else if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
			return MediaContentType.JPEG;
		} else if (format.equalsIgnoreCase("png")) {
			return MediaContentType.PNG;
		} else if (format.equalsIgnoreCase("gif")) {
			return MediaContentType.GIF;
		} else if (format.equalsIgnoreCase("tif") || format.equalsIgnoreCase("tiff")) {
			return MediaContentType.TIF;
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

	public static boolean isCompatiblePhotoFormat(MediaContentType mediaContentType) {
		if (mediaContentType == MediaContentType.JPEG) {
			return true;
		}

		if (mediaContentType == MediaContentType.GIF) {
			return true;
		}

		if (mediaContentType == MediaContentType.TIF) {
			return true;
		}

		if (mediaContentType == MediaContentType.PNG) {
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
			if (mediaContentType  == mediaEncoding.getMediaContentType()) {
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

	public static void addSuppliedStreamUrls(MediaContentType[] suppliedMediaContentTypes, String contextPath, long mediaItemId,
			List<RestfulStream> restfulStreamList) {
		
		for (MediaContentType suppliedMediaContentType : suppliedMediaContentTypes) {
			if (!isFormatPresent(restfulStreamList, suppliedMediaContentType)) {
				String jPlayerContentType = suppliedMediaContentType.getjPlayerContentType();
				restfulStreamList.add(new RestfulStream(jPlayerContentType, prepareUrlStream(contextPath, mediaItemId, jPlayerContentType)));
			}			
		}

	}

	private static boolean isFormatPresent(List<RestfulStream> restfulStreamList, MediaContentType mediaContentType) {
		for (RestfulStream restfulStream : restfulStreamList) {
			String name = mediaContentType.getjPlayerContentType();
			if (restfulStream.getFormat().equalsIgnoreCase(name)) {
				return true;
			}
		}
		
		return false;
	}

	public static long getMediaContentDuration(MediaItem mediaItem) {
		long duration = 0;
		if (mediaItem == null) {
			return duration;
		}
		
		if (mediaItem instanceof Song) {			
			duration = ((Song) mediaItem).getTrackLength();
			return duration;
		}
		
		if (mediaItem instanceof Video) {
//			duration = ((Video) mediaItem.get);
		}
		
		return duration;
	}

	public static MediaItemSequenceType getSequenceType(String sequenceTypeValue) {
		sequenceTypeValue = StringUtils.trimToEmpty(sequenceTypeValue);
		if (StringUtils.isEmpty(sequenceTypeValue)) {
			return MediaItemSequenceType.LATEST;
		}
		
		MediaItemSequenceType[]  mediaItemSequenceTypes = MediaItemSequenceType.values();
		for (MediaItemSequenceType mediaItemSequenceType : mediaItemSequenceTypes) {
			if (sequenceTypeValue.equalsIgnoreCase(mediaItemSequenceType.name())) {
				return mediaItemSequenceType;
			}
		}
		
		return MediaItemSequenceType.LATEST;	
	}

}

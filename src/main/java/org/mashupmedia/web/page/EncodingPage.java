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

package org.mashupmedia.web.page;

public class EncodingPage {

	private String ffmpegFolderPath;
	private boolean isFfMpegFound;
	private String additionalErrorMessage;

	public String getAdditionalErrorMessage() {
		return additionalErrorMessage;
	}

	public void setAdditionalErrorMessage(String additionalErrorMessage) {
		this.additionalErrorMessage = additionalErrorMessage;
	}

	public boolean getIsFfmpegFound() {
		return isFfmpegFound();
	}

	public boolean isFfmpegFound() {
		return isFfMpegFound;
	}

	public void setFfMpegFound(boolean isFfMpegFound) {
		this.isFfMpegFound = isFfMpegFound;
	}

	public String getFfmpegFolderPath() {
		return ffmpegFolderPath;
	}

	public void setFfmpegFolderPath(String ffmpegFolderPath) {
		this.ffmpegFolderPath = ffmpegFolderPath;
	}

}

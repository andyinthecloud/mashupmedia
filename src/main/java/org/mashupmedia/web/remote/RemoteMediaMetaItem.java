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

package org.mashupmedia.web.remote;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.mashupmedia.web.remote.RemoteImage.RemoteImageType;

public class RemoteMediaMetaItem implements Serializable {
	private static final long serialVersionUID = 2688532085007514994L;
	private String remoteId;
	private String name;
	private String profile;
	private Date date;
	private List<RemoteImage> remoteImages;
	private String introduction;
	private boolean error;

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public List<RemoteImage> getRemoteImages() {
		return remoteImages;
	}

	public void setRemoteImages(List<RemoteImage> remoteImages) {
		this.remoteImages = remoteImages;
	}

	public RemoteMediaMetaItem() {
		this.date = new Date();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((remoteId == null) ? 0 : remoteId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((profile == null) ? 0 : profile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteMediaMetaItem other = (RemoteMediaMetaItem) obj;
		if (remoteId == null) {
			if (other.remoteId != null)
				return false;
		} else if (!remoteId.equals(other.remoteId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (profile == null) {
			if (other.profile != null)
				return false;
		} else if (!profile.equals(other.profile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoteMediaMetaItem [id=");
		builder.append(remoteId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", profile=");
		builder.append(profile);
		builder.append(", date=");
		builder.append(date);
		builder.append(", remoteImages=");
		builder.append(remoteImages);
		builder.append("]");
		return builder.toString();
	}

	public RemoteImage getRemoteImage(RemoteImageType remoteImageType) {
		if (remoteImages == null || remoteImages.isEmpty()) {
			return null;
		}

		for (RemoteImage remoteImage : remoteImages) {
			if (remoteImage.getRemoteImageType() == remoteImageType) {
				return remoteImage;
			}
		}
		return null;
	}

}

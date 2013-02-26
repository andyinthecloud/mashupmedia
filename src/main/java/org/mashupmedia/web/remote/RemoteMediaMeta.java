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

public class RemoteMediaMeta implements Serializable {
	private static final long serialVersionUID = 2688532085007514994L;
	private String id;
	private String profile;
	private Date date;
	private List<RemoteImage> remoteImages;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public RemoteMediaMeta() {
		this.date = new Date();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RemoteMediaMeta other = (RemoteMediaMeta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		builder.append("RemoteMediaMeta [id=");
		builder.append(id);
		builder.append(", profile=");
		builder.append(profile);
		builder.append(", date=");
		builder.append(date);
		builder.append(", remoteImages=");
		builder.append(remoteImages);
		builder.append("]");
		return builder.toString();
	}

}

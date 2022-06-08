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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecurityManagerImpl implements SecurityManager {

	@Autowired
	private AdminManager adminManager;

	@Override
	public List<Long> getLoggedInUserGroupIds() {
		User user = AdminHelper.getLoggedInUser();
		return getUserGroupIds(user);
	}

	@Override
	public List<Long> getUserGroupIds(User user) {
		if (user == null) {
			return null;
		}

		user = adminManager.getUser(user.getId());

		Set<Group> groups = user.getGroups();
		if (user.isSystem() || user.isAdministrator()) {
			groups = new HashSet<Group>(adminManager.getGroups());
		}

		if (groups == null || groups.isEmpty()) {
			return null;
		}

		List<Long> groupIds = new ArrayList<Long>();
		for (Group group : groups) {
			groupIds.add(group.getId());
		}

		return groupIds;

	}

	@Override
	public boolean isLoggedInUserInGroup(Collection<Group> groups) {
		if (groups == null || groups.isEmpty()) {
			return false;
		}

		for (Group group : groups) {
			long groupId = group.getId();
			if (hasGroup(groupId)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasGroup(long groupId) {
		List<Long> userGroupIds = getLoggedInUserGroupIds();
		if (userGroupIds == null || userGroupIds.isEmpty()) {
			return false;
		}

		for (Long userGroupId : userGroupIds) {
			if (userGroupId == groupId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canAccessPlaylistMediaItem(PlaylistMediaItem playlistMediaItem) {
		List<Long> groupIds = getLoggedInUserGroupIds();
		if (groupIds == null || groupIds.isEmpty()) {
			return false;
		}

		if (playlistMediaItem == null) {
			return false;
		}

		MediaItem mediaItem = playlistMediaItem.getMediaItem();
		boolean canAccessMediaItem = canAccessMediaItem(mediaItem);
		return canAccessMediaItem;
	}

	@Override
	public boolean canAccessMediaItem(MediaItem mediaItem) {
		if (mediaItem == null) {
			return false;
		}

		Library library = mediaItem.getLibrary();
		Set<Group> groups = library.getGroups();
		if (isLoggedInUserInGroup(groups)) {
			return true;
		}

		return false;
	}

}

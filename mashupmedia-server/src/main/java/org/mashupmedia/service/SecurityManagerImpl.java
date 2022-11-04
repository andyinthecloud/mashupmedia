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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mashupmedia.dto.login.StreamingTokenPayload;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.security.SecurityConstants;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.EncryptService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SecurityManagerImpl implements MashupMediaSecurityManager {

	private final AdminManager adminManager;

	private final EncryptService encryptService;

	private final ObjectMapper objectMapper;

	private final AuthenticationManager authenticationManager;

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

	@Override
	public String generateStreamingToken(String username) {
		StreamingTokenPayload streamingTokenPayload = StreamingTokenPayload
				.builder()
				.username(username)
				.expiresDateTime(LocalDateTime.now().plusHours(SecurityConstants.EXPIRATION_HOURS))
				.build();

		try {
			String streamingToken = objectMapper.writeValueAsString(streamingTokenPayload);
			String encryptedStreamingToken = this.encryptService.encrypt(streamingToken);
			return UriUtils.encodePath(encryptedStreamingToken, StandardCharsets.UTF_8);

		} catch (JsonProcessingException e) {
			throw new MashupMediaRuntimeException("Error generating streaming token", e);
		}

	}

	@Override
	public boolean isStreamingTokenValid(String encodedStreamingToken) {

		StreamingTokenPayload streamingTokenPayload;

		try {
			String encryptedStreamingToken = UriUtils.decode(encodedStreamingToken,
					StandardCharsets.UTF_8).replaceAll(" ", "+");

			String streamingToken = encryptService.decrypt(encryptedStreamingToken);

			streamingTokenPayload = objectMapper.readValue(streamingToken,
					StreamingTokenPayload.class);
			if (streamingTokenPayload == null) {
				return false;
			}
		} catch (JsonProcessingException e) {
			log.info("Error validating streaming token", e);
			return false;
		}

		Assert.notNull(streamingTokenPayload, "streamingTokenPayload should not be null");
		
		User user = adminManager.getUser(streamingTokenPayload.getUsername());

		AdminHelper.setLoggedInUser(user);


		// SecurityContextHolder.getContext().setAuthentication(null);

		// String loggedInUsername = AdminHelper.getLoggedInUser().getUsername();
		// if (!passwordEncoder.matches(streamingTokenPayload.getUsername(), loggedInUsername)) {
		// 	return false;
		
		
		// }

		// AdminHelper.getLoggedInUser()

		LocalDateTime expiresDateTime = streamingTokenPayload.getExpiresDateTime();
		Assert.notNull(expiresDateTime, "expiresDateTime should not be null");

		LocalDateTime now = LocalDateTime.now();
		if (expiresDateTime.isAfter(now.plusHours(SecurityConstants.EXPIRATION_HOURS))) {
			return false;
		}

		return streamingTokenPayload.getExpiresDateTime().isAfter(now);
	}

}

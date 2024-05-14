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

import org.mashupmedia.dto.login.MediaTokenPayload;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.security.SecurityConstants;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.EncryptService;
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

	@Override
	public boolean canAccessPlaylistMediaItem(PlaylistMediaItem playlistMediaItem) {
		if (playlistMediaItem == null) {
			return false;
		}

		return canAccessMediaItem(playlistMediaItem.getMediaItem());
	}

	@Override
	public boolean canAccessMediaItem(MediaItem mediaItem) {
		if (mediaItem == null) {
			return false;
		}

		Library library = mediaItem.getLibrary();
		return library.hasAccess(AdminHelper.getLoggedInUser());
	}

	@Override
	public String generateMediaToken(String username) {
		MediaTokenPayload mediaTokenPayload = MediaTokenPayload
				.builder()
				.username(username)
				.expiresDateTime(LocalDateTime.now().plusHours(SecurityConstants.EXPIRATION_HOURS))
				.build();

		try {
			String mediaToken = objectMapper.writeValueAsString(mediaTokenPayload);
			String encryptedMediaToken = this.encryptService.encrypt(mediaToken);
			return UriUtils.encodePath(encryptedMediaToken, StandardCharsets.UTF_8);

		} catch (JsonProcessingException e) {
			throw new MashupMediaRuntimeException("Error generating media token", e);
		}

	}

	@Override
	public boolean isMediaTokenValid(String encodedMediaToken) {

		MediaTokenPayload mediaTokenPayload;

		try {
			String encryptedMediaToken = UriUtils.decode(encodedMediaToken,
					StandardCharsets.UTF_8).replaceAll(" ", "+");

			String mediaToken = encryptService.decrypt(encryptedMediaToken);

			mediaTokenPayload = objectMapper.readValue(mediaToken,
					MediaTokenPayload.class);
			if (mediaTokenPayload == null) {
				return false;
			}
		} catch (JsonProcessingException e) {
			log.info("Error validating media token", e);
			return false;
		}

		Assert.notNull(mediaTokenPayload, "mediaTokenPayload should not be null");
		
		User user = adminManager.getUser(mediaTokenPayload.getUsername());

		AdminHelper.setLoggedInUser(user);

		LocalDateTime expiresDateTime = mediaTokenPayload.getExpiresDateTime();
		Assert.notNull(expiresDateTime, "expiresDateTime should not be null");

		LocalDateTime now = LocalDateTime.now();
		if (expiresDateTime.isAfter(now.plusHours(SecurityConstants.EXPIRATION_HOURS))) {
			return false;
		}

		return mediaTokenPayload.getExpiresDateTime().isAfter(now);
	}

}

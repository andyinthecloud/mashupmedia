package org.mashupmedia.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.dao.GroupDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.dao.RoleDao;
import org.mashupmedia.dao.UserDao;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.EncryptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminManagerImpl implements AdminManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private PlaylistDao playlistDao;

	@Override
	public User getUser(String username) {
		User user = userDao.getUser(username);
		initialisePlaylistMediaItem(user);
		return user;
	}

	private void initialisePlaylistMediaItem(User user) {
		if (user == null) {
			return;
		}
		
		long playlistMediaItemId = user.getPlaylistMediaItemId();
		PlaylistMediaItem playlistMediaItem = playlistDao.getPlaylistMediaItem(playlistMediaItemId);
		user.setPlaylistMediaItem(playlistMediaItem);
	}
	
	
	
	@Override
	public User getUser(long userId) {
		User user = userDao.getUser(userId);
		return user;
	}

	@Override
	public void saveUser(User user) {
		Date date = new Date();
		long userId = user.getId();
		String username = user.getUsername();
		String password = user.getPassword();		

		// All users should have the user role to access the application
		Set<Role> roles = user.getRoles();
		if (roles == null) {
			roles = new HashSet<Role>();
		}
		Role userRole = getRole(AdminHelper.ROLE_USER_IDNAME);
		roles.add(userRole);
		user.setRoles(roles);

		if (userId == 0) {
			user.setCreatedOn(date);
		} else {
			User savedUser = getUser(userId);
			user.setPassword(savedUser.getPassword());
			user.setPlaylistMediaItem(savedUser.getPlaylistMediaItem());
		}

		user.setUpdatedOn(date);
		userDao.saveUser(user);

		if (StringUtils.isNotBlank(password)) {
			logger.info("Updating user password...");
			updatePassword(username, password);
		}

	}
	
	@Override
	public void updateUser(User user) {
		if (user.getId() == 0) {
			throw new MashupMediaRuntimeException("Can only update an existing user.");
		}
		
		
		PlaylistMediaItem playlistMediaItem = user.getPlaylistMediaItem();
		long playlistMediaItemId = playlistMediaItem.getId();
		user.setPlaylistMediaItemId(playlistMediaItemId);
		userDao.saveUser(user);
	}

	@Override
	public int getTotalUsers() {
		int totalUsers = userDao.getTotalUsers();
		return totalUsers;
	}

	@Override
	public void saveRole(Role role) {
		String idName = StringUtils.trimToEmpty(role.getIdName());
		Role savedRole = roleDao.getRole(idName);
		if (savedRole != null) {
			logger.debug("Role is already saved with idName: " + idName);
			return;
		}
		if (role.getId() == 0) {
			role.setCreatedOn(new Date());
		}

		roleDao.saveRole(role);

	}

	@Override
	public void updatePassword(String username, String password) {
		User user = getUser(username);
		if (user == null) {
			throw new MashupMediaRuntimeException("Could not find user with username: " + username);
		}

		String encodedPassword = EncryptionHelper.encodePassword(password);
		user.setPassword(encodedPassword);
		userDao.saveUser(user);
	}

	@Override
	public void saveGroup(Group group) {
		if (group.getId() == 0) {
			group.setCreatedOn(new Date());
		}
		groupDao.saveGroup(group);
	}

	@Override
	public List<Group> getGroups() {
		List<Group> groups = groupDao.getGroups();
		return groups;
	}

	@Override
	public Group getGroup(long groupId) {
		Group group = groupDao.getGroup(groupId);
		return group;
	}

	@Override
	public List<User> getUsers() {
		List<User> users = userDao.getUsers();
		return users;
	}

	@Override
	public List<Role> getRoles() {
		List<Role> roles = roleDao.getRoles();
		return roles;
	}

	@Override
	public Role getRole(String idName) {
		Role role = roleDao.getRole(idName);
		return role;
	}

	@Override
	public void deleteUser(long userId) {
		User user = getUser(userId);
		List<Playlist> playlists = playlistDao.getPlaylistsForCurrentUser(userId, PlaylistType.ALL);
		for (Playlist playlist : playlists) {
			playlistDao.deletePlaylist(playlist);
		}

		userDao.deleteUser(user);
	}

	@Override
	public void deleteGroup(long groupId) {
		Group group = getGroup(groupId);
		groupDao.deleteGroup(group);
	}

	@Override
	public void  initialiseAdminUser() {
		User user = new User();
		user.setName(MashUpMediaConstants.ADMIN_USER_DEFAULT_NAME);
		user.setUsername(MashUpMediaConstants.ADMIN_USER_DEFAULT_USERNAME);
		user.setPassword(MashUpMediaConstants.ADMIN_USER_DEFAULT_PASSWORD);

		user.setEnabled(true);
		user.setEditable(false);
		
		Set<Role> roles = new HashSet<Role>(getRoles());
		user.setRoles(roles);

		List<Group> groups = getGroups();
		user.setGroups(new HashSet<Group>(groups));
		saveUser(user);
	}
	
	@Override
	public void initialiseSystemUser() {
		User user = new User();
		user.setName(MashUpMediaConstants.SYSTEM_USER_DEFAULT_NAME);
		user.setUsername(MashUpMediaConstants.SYSTEM_USER_DEFAULT_USERNAME);
		user.setPassword(MashUpMediaConstants.SYSTEM_USER_DEFAULT_PASSWORD);

		user.setEnabled(true);
		user.setEditable(false);
		user.setSystem(true);
		
		Set<Role> roles = new HashSet<Role>(getRoles());
		user.setRoles(roles);

		List<Group> groups = getGroups();
		user.setGroups(new HashSet<Group>(groups));
		saveUser(user);		
	}
	
	@Override
	public User getSystemUser() {
		User systemUser = getUser(MashUpMediaConstants.SYSTEM_USER_DEFAULT_USERNAME);		
		return systemUser;
	}
}

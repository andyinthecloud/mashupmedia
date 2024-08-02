package org.mashupmedia.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.dao.RoleDao;
import org.mashupmedia.dao.UserDao;
import org.mashupmedia.eums.PremiumType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.account.Premium;
import org.mashupmedia.model.account.Role;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.repository.admin.PremiumRepository;
import org.mashupmedia.repository.admin.UserRepository;
import org.mashupmedia.repository.playlist.PlaylistRepository;
import org.mashupmedia.util.LibraryHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AdminManagerImpl implements AdminManager {

	private final UserDao userDao;
	private final RoleDao roleDao;
	private final PlaylistDao playlistDao;
	private final PlaylistRepository playlistRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final PremiumRepository premiumRepository;
	// private LibraryManager libraryManager;
	// private final LibraryDao libraryDao;

	@Override
	public User getUser(String username) {
		User user = userDao.getUser(username);
		return user;
	}

	@Override
	public User getUser(long userId) {
		User user = userDao.getUser(userId);
		return user;
	}

	private long getUserId(String username) {
		User user = userDao.getUser(username);
		return user != null ? user.getId() : 0;
	}

	@Override
	public void saveUser(User user) {
		Date date = new Date();
		String username = user.getUsername();
		// String password = user.getPassword();
		long userId = getUserId(username);
		user.setId(userId);

		processRoles(user);

		if (userId == 0) {
			createUser(user, date);
			// user.setCreatedOn(date);
			// user.setLibraryFolderName(RandomStringUtils.randomAlphanumeric(15));

		} else {
			updateUser(user);
			// User savedUser = getUser(userId);
			// user.setCreatedOn(savedUser.getCreatedOn());
			// user.setPassword(savedUser.getPassword());
		}

		// user.setUpdatedOn(date);
		// userDao.saveUser(user);

		

		userDao.saveUser(user);

		// if (userId == 0 && StringUtils.isNotBlank(password)) {
		// 	log.info("Assigning user password");
		// 	updatePassword(username, password);
		// 	userDao.saveUser(user);
		// }

	}

	private void createUser(User user, Date date) {
		user.setCreatedOn(date);
		user.setFolderName(RandomStringUtils.randomAlphanumeric(15));

		String password = user.getPassword();
		if (StringUtils.isBlank(password)) {
			password = RandomStringUtils.randomAlphanumeric(15);
		}
		user.setPassword(passwordEncoder.encode(password));

		MusicLibrary privateLibrary = createLibrary("Private", user, true);
		user.getLibraries().add(privateLibrary);
		// libraryDao.saveLibrary(privateLibrary);

		MusicLibrary publicLibrary = createLibrary("Public", user, false);
		// libraryDao.saveLibrary(publicLibrary);
		user.getLibraries().add(publicLibrary);

		user.setUpdatedOn(date);

		Premium premium = premiumRepository.findByNameIgnoreCase(PremiumType.FREE.name());
		user.setPremium(premium);	

		// userDao.saveUser(user);

	}


	@Override
	public void updateUser(User user) {
		assertHasAtLeastOneAdministrator(user);

		User savedUser = getUser(user.getId());
		user.setCreatedOn(savedUser.getCreatedOn());
		user.setPassword(savedUser.getPassword());
		user.setUpdatedOn(new Date());
		// userDao.saveUser(user);
	}


	private void processRoles(User user) {
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			return;
		}

		Set<Role> roles = user.getRoles().stream()
				.map(r -> getRole(r.getIdName()))
				.collect(Collectors.toSet());
		user.setRoles(roles);
	}

	// @Override
	// public void updateUser(User user) {
	// 	if (user.getId() == 0) {
	// 		throw new MashupMediaRuntimeException("Can only update an existing user.");
	// 	}

	// 	assertHasAtLeastOneAdministrator(user);
	// 	userDao.saveUser(user);
	// }

	private void assertHasAtLeastOneAdministrator(User user) {

		if (user.isAdministrator()) {
			return;
		}

		if (userRepository.getTotalUserAdministrators() < 1) {
			throw new MashupMediaRuntimeException("At least one adminstrator is mandatory");
		}
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
			log.debug("Role is already saved with idName: " + idName);
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

		// String encodedPassword = EncryptionHelper.encodePassword(password);
		String encodedPassword = passwordEncoder.encode(password);
		user.setPassword(encodedPassword);
		// userDao.saveUser(user);
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
		assertHasAtLeastOneAdministrator(user);

		List<Playlist> playlists = playlistDao.getPlaylistsForCurrentUser(userId, null);
		for (Playlist playlist : playlists) {
			playlistRepository.delete(playlist);
		}

		userDao.deleteUser(user);
	}

	@Override
	public void initialiseAdminUser() {

		String name = MashUpMediaConstants.ADMIN_USER_DEFAULT_NAME;
		User user = User.builder()
				.name(name)
				.username(MashUpMediaConstants.ADMIN_USER_DEFAULT_USERNAME)
				.password(MashUpMediaConstants.ADMIN_USER_DEFAULT_PASSWORD)
				.enabled(true)
				.roles(new HashSet<Role>(getRoles()))
				.build();

		saveUser(user);

		// Playlist playlist = Playlist.builder()
		// 		.name(name + "'s " + MessageHelper.getMessage("music.playlist.default.name"))
		// 		.userDefault(true)
		// 		.createdBy(user)
		// 		.mediaTypeValue(MashupMediaType.MUSIC.name())
		// 		.playlistMediaItems(new HashSet<>())
		// 		.build();
		// playlistDao.savePlaylist(playlist);

		// MusicLibrary privateLibrary = createLibrary("Private", user, true);
		// libraryDao.saveLibrary(privateLibrary);

		// MusicLibrary publicLibrary = createLibrary("Private", user, true);
		// libraryDao.saveLibrary(publicLibrary);
	}

	private MusicLibrary createLibrary(String name, User user, boolean isPrivateAccess) {
		Date date = new Date();
		return MusicLibrary.builder()
				.user(user)
				.createdOn(date)
				.enabled(true)
				.name(name)
				.privateAccess(isPrivateAccess)
				.path(LibraryHelper.getLibraryFolderName(user.getFolderName(), name))
				.build();

	}



	@Override
	public void initialiseSystemUser() {
		User user = User.builder()
				.name(MashUpMediaConstants.SYSTEM_USER_DEFAULT_NAME)
				.username(MashUpMediaConstants.SYSTEM_USER_DEFAULT_USERNAME)
				.password(MashUpMediaConstants.SYSTEM_USER_DEFAULT_PASSWORD)
				.enabled(true)
				.system(true)
				.roles(new HashSet<>(getRoles()))
				.build();

		saveUser(user);
	}

	@Override
	public User getSystemUser() {
		User systemUser = getUser(MashUpMediaConstants.SYSTEM_USER_DEFAULT_USERNAME);
		return systemUser;
	}

}

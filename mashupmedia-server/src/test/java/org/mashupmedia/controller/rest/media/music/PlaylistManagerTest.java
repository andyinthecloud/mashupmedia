package org.mashupmedia.controller.rest.media.music;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dao.GroupDao;
import org.mashupmedia.dao.GroupDaoImpl;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.dao.LibraryDaoImpl;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.MusicDaoImpl;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.dao.PlaylistDaoImpl;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({
        PlaylistDaoImpl.class,
        GroupDaoImpl.class,
        LibraryDaoImpl.class,
        MusicDaoImpl.class })
public class PlaylistManagerTest {

    @Autowired
    private PlaylistDao playlistDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private LibraryDao libraryDao;

    @Autowired
    private MusicDao musicDao;

    @Test
    void whenLoadPlaylist_thenNoDuplicates() {

        createAndPersistGroups(5);

        MusicLibrary musicLibrary = createAndPersistMusicLibrary();
        Playlist playlist = createAndPersistPlaylistWithAlbum(musicLibrary);

        assertTrue(10 == playlist.getPlaylistMediaItems().size());
    }

    private Collection<Group> createAndPersistGroups(int total) {

        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Group group = new Group();
            group.setName("Group " + i);
            groupDao.saveGroup(group);
            groups.add(group);
        }

        return groups;
    }

    private MusicLibrary createAndPersistMusicLibrary() {
        MusicLibrary musicLibrary = new MusicLibrary();
        musicLibrary.setName("name");
        musicLibrary.setEnabled(true);
        musicLibrary.setGroups(new HashSet<>(groupDao.getGroups()));
        libraryDao.saveLibrary(musicLibrary);
        return musicLibrary;
    }

    private Artist createAndPersistArtist() {
        Artist artist = new Artist();
        artist.setFolderName("folderName");
        artist.setIndexLetter("i");
        artist.setName("name");
        musicDao.saveArtist(artist);
        return artist;

    }

    private Album createAndPersistAlbum(Artist artist) {

        // Track track = createTrack();

        Album album = new Album();
        album.setArtist(artist);
        album.setFolderName("folderName");
        album.setName("name");
        // album.setTracks(tracks);
        musicDao.saveAlbum(album);

        return album;
    }

    private Track createAndPersistTrack(String title, Album album, Artist artist, Library library) {
        Track track = new Track();
        track.setLibrary(library);
        track.setAlbum(album);
        track.setArtist(artist);
        track.setEnabled(true);
        track.setDisplayTitle("displayTitle " + title);
        track.setFileName("fileName");
        track.setPath("path" + title);
        track.setTitle(title);
        musicDao.saveTrack(track);
        return track;
    }

    private Playlist createAndPersistPlaylistWithAlbum(MusicLibrary musicLibrary) {
        Artist artist = createAndPersistArtist();
        Album album = createAndPersistAlbum(artist);
        

        Playlist playlist = new Playlist();
        playlist.setMashupMediaType(MashupMediaType.MUSIC);
        playlist.setName("playlist");
        playlist.setPlaylistMediaItems(new HashSet<>());
        playlistDao.savePlaylist(playlist);

        // List<PlaylistMediaItem> playlistMediaItems = new ArrayList<>(); 

        for (int i = 0; i < 10; i++) {
            Track track = createAndPersistTrack("Track " + i, album, artist, musicLibrary);
            PlaylistMediaItem playlistMediaItem = new PlaylistMediaItem();
            playlistMediaItem.setMediaItem(track);
            playlistMediaItem.setPlaylist(playlist);
            playlistMediaItem.setRanking(i);

            playlist.getPlaylistMediaItems().add(playlistMediaItem);
            // entityManager.persist(playlistMediaItem);
        }

        // playlistDao.savePlaylist(playlist);

        // Playlist savedPLaylist =  entityManager.getId(1, Playlist.class);

        playlist = playlistDao.getPlaylist(playlist.getId());
        return playlist;
    }
}

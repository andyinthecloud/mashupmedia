package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.repository.media.music.MusicAlbumRepository;
import org.mashupmedia.repository.media.music.SongRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MusicLibraryUpdateManagerImplTest {

    @Mock
    private MusicDao musicDao;

    @Mock
    private MediaDao mediaDao;

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private MusicAlbumRepository musicAlbumRepository;

    @Mock
    private LibraryManager libraryManager;

    @Mock
    private AlbumArtManager albumArtManager;

    @InjectMocks
    private MusicLibraryUpdateManagerImpl musicLibraryUpdateManagerImpl;



    @Test
    void givenAMusicFolder_whenSaveSongs_thenGetFolderName() throws Exception {

        Mockito.when(songRepository.findByLibraryIdAndPathAndLastModifiedOn(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.doNothing().when(musicDao).saveSong(Mockito.isA(Song.class), Mockito.anyBoolean());
        Mockito.doNothing().when(libraryManager).saveMediaItemLastUpdated(Mockito.anyLong());
        Mockito.when(albumArtManager.getAlbumArtImage(Mockito.isA(MusicLibrary.class), Mockito.isA(Song.class))).thenReturn(new AlbumArtImage());

        Mockito.when(artistRepository.findArtistByNameIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(musicAlbumRepository.findByArtistNameAndAlbumNameIgnoreCase(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());


        String path = "C:\\stuff\\data\\mm\\m";

        MusicLibrary musicLibrary = createMusicLibrary(path);
        musicLibraryUpdateManagerImpl.prepareSongs(new Date(), new ArrayList<>(), new File(path), musicLibrary, null, null);
        
    
    }

    private MusicLibrary createMusicLibrary(String path) {
        MusicLibrary library = new MusicLibrary();
        library.setId(1);

        Location location = new Location();
        location.setPath(path);
        library.setLocation(location);

        return library;
    }

}

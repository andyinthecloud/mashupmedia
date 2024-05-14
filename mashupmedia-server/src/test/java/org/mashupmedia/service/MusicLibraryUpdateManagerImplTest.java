package org.mashupmedia.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.repository.media.music.MusicAlbumRepository;
import org.mashupmedia.repository.media.music.TrackRepository;
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
    private TrackRepository trackRepository;

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
    void givenAMusicFolder_whenSaveTracks_thenGetFolderName() throws Exception {

        Mockito.when(trackRepository.findByLibraryIdAndPathAndLastModifiedOn(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.doNothing().when(musicDao).saveTrack(Mockito.isA(Track.class), Mockito.anyBoolean());
        Mockito.doNothing().when(libraryManager).saveMediaItemLastUpdated(Mockito.anyLong());
        Mockito.when(albumArtManager.getMetaImage(Mockito.isA(MusicLibrary.class), Mockito.isA(Track.class))).thenReturn(new MetaImage());

        Mockito.when(artistRepository.findArtistByNameIgnoreCase(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(musicAlbumRepository.findByArtistNameAndAlbumNameIgnoreCase(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());


        String path = "C:\\stuff\\data\\mm\\m";

        MusicLibrary musicLibrary = createMusicLibrary(path);
        musicLibraryUpdateManagerImpl.prepareTracks(new Date(), new ArrayList<>(), new File(path), musicLibrary, null, null);
        
    
    }

    private MusicLibrary createMusicLibrary(String path) {
        MusicLibrary library = new MusicLibrary();
        library.setId(1);
        library.setPath(path);
        return library;
    }

}

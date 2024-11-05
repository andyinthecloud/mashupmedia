package org.mashupmedia.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.exception.UserStorageException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.MetaTrack;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.media.social.SocialConfiguration;
import org.mashupmedia.repository.media.music.LibraryRepository;
import org.mashupmedia.service.media.audio.AudioMetaManager;
import org.mashupmedia.service.storage.StorageManager;
import org.mashupmedia.service.transcode.TranscodeAudioManager;
import org.mashupmedia.service.transcode.TranscodeImageManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.GenreHelper;
import org.mashupmedia.util.MediaContentHelper;
import org.mashupmedia.util.MetaEntityHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicResourceManagerImpl implements MusicResourceManager {

    private final MusicManager musicManager;
    private final TranscodeImageManager transcodeManager;
    private final StorageManager storageManager;
    private final TranscodeAudioManager transcodeAudioManager;
    private final AudioMetaManager audioMetaManager;
    private final LibraryRepository libraryRepository;

    @Override
    public MetaImage storeArtistImage(long artistId, MultipartFile multipartFile) throws UserStorageException {
        Artist artist = musicManager.getArtist(artistId);
        MetaImage metaImage = processImage(artist.getUser(), multipartFile, artist.getName());

        MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
        Set<MetaImage> metaImages = metaImageHelper.addMetaEntity(metaImage, artist.getMetaImages());
        artist.setMetaImages(metaImages);

        musicManager.saveArtist(artist);

        return metaImage;
    }

    @Override
    public MetaImage storeAlbumImage(long albumId, MultipartFile multipartFile) throws UserStorageException {
        Album album = musicManager.getAlbum(albumId);
        Artist artist = album.getArtist();
        MetaImage metaImage = processImage(artist.getUser(), multipartFile, album.getName());
        MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
        Set<MetaImage> metaImages = metaImageHelper.addMetaEntity(metaImage, album.getMetaImages());
        album.setMetaImages(metaImages);

        musicManager.saveAlbum(album);

        return metaImage;
    }

    private MetaImage processImage(User user, MultipartFile multipartFile, String name) throws UserStorageException {

        AdminHelper.checkAccess(user);
        storageManager.checkUserStorage(multipartFile.getSize());

        String fileExtension = FileHelper.getFileExtension(multipartFile.getOriginalFilename());
        MediaContentType mediaContentType = MediaContentType.getMediaContentType(fileExtension);

        if (!MediaContentHelper.isCompatiblePhotoFormat(mediaContentType)) {
            throw new MashupMediaRuntimeException("Image content type is incompatible");
        }

        MetaImage metaImage = new MetaImage();
        metaImage.setName(name);
        metaImage.setMimeType(mediaContentType.getMimeType());

        try {
            // InputStream inputStream = multipartFile.getInputStream();
            Path tempImagePath = user.createTempResourcePath();
            Files.write(tempImagePath, multipartFile.getBytes());

            Path tempProcessedImagePath = transcodeManager.processImage(tempImagePath, MediaContentType.IMAGE_JPG);
            String imagePath = storageManager.store(tempProcessedImagePath);
            metaImage.setUrl(imagePath);
            Files.delete(tempProcessedImagePath);

            Path tempThumbnailPath = transcodeManager.processThumbnail(tempImagePath, MediaContentType.IMAGE_JPG);
            String thumbnailPath = storageManager.store(tempThumbnailPath);
            metaImage.setThumbnailUrl(thumbnailPath);
            Files.delete(tempThumbnailPath);

            Files.delete(tempImagePath);
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Error deleting path", e);
        }

        return metaImage;
    }

    @Override
    public void storeTrack(long libraryId, long albumId, Integer year, String genreIdName, MultipartFile multipartFile)
            throws UserStorageException {

        Library library = libraryRepository.getReferenceById(libraryId);
        Assert.notNull(library, "Expecting an library");

        Album album = musicManager.getAlbum(albumId);
        Assert.notNull(album, "Expecting an album");

        Artist artist = album.getArtist();
        AdminHelper.checkAccess(artist.getUser());
        storageManager.checkUserStorage(multipartFile.getSize());

        User user = AdminHelper.getLoggedInUser();
        Path uploadPath = user.getUserTempPath().resolve(multipartFile.getOriginalFilename());
        try {
            Files.write(uploadPath, multipartFile.getBytes());
        } catch (IOException e) {
            throw new UserStorageException("Error saving file to temporary folder", e);
        }

        MetaTrack metaTrack = audioMetaManager.getMetaTrack(uploadPath);

        Genre genre = StringUtils.isBlank(genreIdName)
                ? metaTrack.getGenre()
                : GenreHelper.getGenre(genreIdName);

        Track track = Track.builder()
                .album(album)
                .title(metaTrack.getTitle())
                .trackLength(metaTrack.getLength())
                .trackNumber(metaTrack.getNumber())
                .genre(genre)
                .trackYear(year != null ? year : metaTrack.getYear())
                .socialConfiguration(SocialConfiguration.builder().build())
                .library(library)
                .build();

        musicManager.saveTrack(track);

        transcodeAudioManager.processTrack(track, uploadPath.toAbsolutePath().toString());

        try {
            Files.delete(uploadPath);
        } catch (IOException e) {
            throw new UserStorageException("Error deleting uploaded file", e);
        }

    }

}

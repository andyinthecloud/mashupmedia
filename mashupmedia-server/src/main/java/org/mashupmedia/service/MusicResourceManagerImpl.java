package org.mashupmedia.service;

import java.nio.file.Path;
import java.util.Set;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.exception.UserStorageException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.storage.StorageManager;
import org.mashupmedia.service.transcode.TranscodeImageManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaContentHelper;
import org.mashupmedia.util.MetaEntityHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicResourceManagerImpl implements MusicResourceManager {

    private final MusicManager musicManager;
    private final TranscodeImageManager transcodeManager;
    private final StorageManager storageManager;

    @Override
    public MetaImage storeArtistImage(long artistId, MultipartFile multipartFile) throws UserStorageException{
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

    private MetaImage processImage(User user, MultipartFile multipartFile, String name) throws UserStorageException{
        
        AdminHelper.checkAccess(user);
        storageManager.checkUserStorage(multipartFile.getSize());
        
        String fileExtension = FileHelper.getFileExtension(multipartFile.getOriginalFilename());
        MediaContentType mediaContentType = MediaContentHelper.getMediaContentType(fileExtension);

        if (!MediaContentHelper.isCompatiblePhotoFormat(mediaContentType)) {
            throw new MashupMediaRuntimeException("Image content type is incompatible");
        }

        MetaImage metaImage = new MetaImage();
        metaImage.setName(name);
        metaImage.setContentType(mediaContentType.getContentType());

        Path tempImagePath = transcodeManager.processImage(multipartFile);
        String imagePath = storageManager.store(tempImagePath);
        metaImage.setUrl(imagePath);

        Path tempThumbnailPath = transcodeManager.processThumbnail(multipartFile);
        String thumbnailPath = storageManager.store(tempThumbnailPath);
        metaImage.setThumbnailUrl(thumbnailPath);
    
        return metaImage;
    }

}

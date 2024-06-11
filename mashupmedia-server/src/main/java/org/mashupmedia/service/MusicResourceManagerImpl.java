package org.mashupmedia.service;

import java.nio.file.Path;
import java.util.Set;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.media.MetaImage;
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
    public MetaImage storeArtistImage(long artistId, MultipartFile multipartFile) {

        String fileExtension = FileHelper.getFileExtension(multipartFile.getOriginalFilename());
        MediaContentType mediaContentType = MediaContentHelper.getMediaContentType(fileExtension);

        if (!MediaContentHelper.isCompatiblePhotoFormat(mediaContentType)) {
            throw new MashupMediaRuntimeException("Image content type is incompatible");
        }

        Artist artist = musicManager.getArtist(artistId);
        AdminHelper.checkAccess(artist.getUser());

        MetaImage metaImage = new MetaImage();
        metaImage.setName(artist.getName());
        metaImage.setContentType(mediaContentType.getContentType());

        Path tempArtistImagePath = transcodeManager.processImage(multipartFile);
        String artistImagePath = storageManager.store(tempArtistImagePath);
        metaImage.setUrl(artistImagePath);

        Path tempArtistThumbnailPath = transcodeManager.processThumbnail(multipartFile);
        String artistThumbnailPath = storageManager.store(tempArtistThumbnailPath);
        metaImage.setThumbnailUrl(artistThumbnailPath);

        MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
        Set<MetaImage> metaImages = metaImageHelper.addMetaEntity(metaImage, artist.getMetaImages());
        artist.setMetaImages(metaImages);

        musicManager.saveArtist(artist);
        
        return metaImage;
    }

}

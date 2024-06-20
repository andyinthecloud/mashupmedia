package org.mashupmedia.service;

import org.mashupmedia.exception.UserStorageException;
import org.mashupmedia.model.media.MetaImage;
import org.springframework.web.multipart.MultipartFile;

public interface MusicResourceManager {


    public MetaImage storeArtistImage(long artistId, MultipartFile multipartFile) throws UserStorageException;

    public MetaImage storeAlbumImage(long albumId, MultipartFile file) throws UserStorageException;

}

package org.mashupmedia.service;

import org.mashupmedia.model.media.MetaImage;
import org.springframework.web.multipart.MultipartFile;

public interface MusicResourceManager {


    public MetaImage storeArtistImage(long artistId, MultipartFile multipartFile);

}

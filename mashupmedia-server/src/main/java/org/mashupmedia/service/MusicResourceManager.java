package org.mashupmedia.service;

import org.springframework.web.multipart.MultipartFile;

public interface MusicResourceManager {


    public void storeArtistImage(long artistId, MultipartFile multipartFile);

}

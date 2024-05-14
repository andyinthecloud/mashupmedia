package org.mashupmedia.controller.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mashupmedia.comparator.MetaEntityComparator;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaContentHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import jakarta.annotation.Nullable;

public abstract class MetaImageController {

    protected abstract String getDefaultImagePath();

    private static final MediaContentType DEFAULT_CONTENT_TYPE = MediaContentType.IMAGE_JPG;

    protected List<MetaImage> getSortedMetaImages(Set<MetaImage> metaImages) {
        List<MetaImage> sortedImages = new ArrayList<>();
        if (metaImages == null || metaImages.isEmpty()) {
            return sortedImages;
        }

        sortedImages.addAll(metaImages);
        Collections.sort(sortedImages, new MetaEntityComparator());
        return sortedImages;
    }


    protected MetaResource getHighlightedMetaImage(Set<MetaImage> metaImages, ImageType imageType,
            @Nullable Integer index) {
        if (metaImages == null || metaImages.isEmpty()) {
            return getDefaultMetaImage();
        }

        List<MetaImage> sortedImages = getSortedMetaImages(metaImages);

        int rank = index == null
                ? 0
                : index;

        MetaImage metaImage = sortedImages.stream()
                .filter(mi -> mi.getRank() == rank)
                .findAny()
                .orElse(sortedImages.get(0));

        if (metaImage == null) {
            return getDefaultMetaImage();
        }

        String path = getPath(metaImage, imageType);

        return MetaResource.builder()
                .mediaContentType(MediaContentHelper.getMediaContentType(metaImage.getContentType()))
                .resource(new FileSystemResource(path))
                .build();
    }

    protected String getPath(MetaImage metaImage, ImageType imageType) {
        if (metaImage == null) {
            return null;
        }

        return imageType == ImageType.THUMBNAIL
                ? metaImage.getUrl()
                : metaImage.getThumbnailUrl();
    }

    private MetaResource getDefaultMetaImage() {
        return MetaResource.builder()
                .mediaContentType(DEFAULT_CONTENT_TYPE)
                .resource(new ClassPathResource(getDefaultImagePath()))
                .build();
    }

}

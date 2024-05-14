package org.mashupmedia.model.media;

import org.mashupmedia.model.MetaEntity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "meta_images")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetaImage extends MetaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meta_images_generator")
	@SequenceGenerator(name = "meta_images_generator", sequenceName = "meta_images_seq", allocationSize = 1)
	@EqualsAndHashCode.Exclude
	private long id;
	private String name;
	private String url;
	@EqualsAndHashCode.Exclude
	private String thumbnailUrl;
	@EqualsAndHashCode.Exclude
	private String contentType;
	@EqualsAndHashCode.Exclude
	private int rank;

	@Override
	public void updateValues(MetaEntity updatedEntity) {
        if (updatedEntity instanceof MetaImage metaImage) {
            setName(metaImage.getName());
			setUrl(metaImage.getUrl());
			setThumbnailUrl(metaImage.getThumbnailUrl());
			setContentType(metaImage.getContentType());
			setRank(metaImage.getRank());
        }		
	}

}

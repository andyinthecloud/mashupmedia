package org.mashupmedia.model.media;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.MetaEntity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Builder
public class MetaImage extends MetaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meta_images_generator")
	@SequenceGenerator(name = "meta_images_generator", sequenceName = "meta_images_seq", allocationSize = 1)
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
			if (StringUtils.isNotBlank(metaImage.getName())) {
				setName(metaImage.getName());
			}

			if (StringUtils.isNotBlank(metaImage.getUrl())) {
				setUrl(metaImage.getUrl());
			}

			if (StringUtils.isNotBlank(metaImage.getThumbnailUrl())) {
				setThumbnailUrl(metaImage.getThumbnailUrl());
			}

			if (StringUtils.isNotBlank(metaImage.getContentType())) {
				setContentType(metaImage.getContentType());
			}

			setRank(metaImage.getRank());
        }
	}

}

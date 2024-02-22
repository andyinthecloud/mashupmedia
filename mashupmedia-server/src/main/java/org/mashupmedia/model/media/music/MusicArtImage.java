package org.mashupmedia.model.media.music;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@Table(name = "music_art_images")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MusicArtImage {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Exclude
	private long id;
	private String name;	
	private String url;
	@EqualsAndHashCode.Exclude
	private String thumbnailUrl;
	@EqualsAndHashCode.Exclude
	private String contentType;
}

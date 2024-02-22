package org.mashupmedia.model.library;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@Entity
@Table(name = "video_libraries")
@Cacheable
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class VideoLibrary extends Library {

	private static final long serialVersionUID = -7784201711543047031L;

	public enum VideoDeriveTitleType {
		USE_FOLDER_NAME, USE_FILE_NAME, USE_FOLDER_AND_FILE_NAME
	}

	private String videoDeriveTitle;
	@Builder.Default
	private boolean encodeVideoOnDemand = false;
	
	// public VideoLibrary() {
	// 	// By default set the video encoding to on demand
	// 	this.encodeVideoOnDemand = true;
	// }

	@Override
	public LibraryType getLibraryType() {
		return LibraryType.VIDEO;
	}


}

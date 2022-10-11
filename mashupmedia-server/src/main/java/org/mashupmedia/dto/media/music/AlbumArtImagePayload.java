package org.mashupmedia.dto.media.music;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AlbumArtImagePayload {
    private String name;
	private String url;
	private String thumbnailUrl;
    private String contentType;
    
}

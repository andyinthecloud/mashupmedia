package org.mashupmedia.dto.media.music;

import java.util.List;

import org.mashupmedia.dto.media.ExternalLinkPayload;
import org.mashupmedia.dto.media.MetaEntityPayload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AlbumPayload {
    private long id;
    @NotBlank(message = "The album name should not be empty.")
    private String name;
    private String summary;
    private List<ExternalLinkPayload> externalLinkPayloads;
    private List<MetaEntityPayload> metaImagePayloads;
}

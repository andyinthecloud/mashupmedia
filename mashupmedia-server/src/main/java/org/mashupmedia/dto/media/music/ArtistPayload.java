package org.mashupmedia.dto.media.music;

import java.util.List;

import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.dto.media.ExternalLinkPayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ArtistPayload {
    private long id;
    private String name;
    private String profile;
    private UserPayload userPayload;
    private List<ExternalLinkPayload> externalLinkPayloads; 
}

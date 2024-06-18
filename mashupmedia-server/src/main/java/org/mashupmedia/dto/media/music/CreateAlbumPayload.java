package org.mashupmedia.dto.media.music;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateAlbumPayload {
    @NotBlank(message = "The artist name should not be empty.")
    private String name;
    private long artistId;
}

package org.mashupmedia.dto.library;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.mashupmedia.dto.share.NameValuePayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class LibraryPayload {

    private long id;
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Path should not be empty")
    private String path; 
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
    private boolean enabled;
    private LocalDateTime lastSuccessfulScanOn;
    @NotEmpty(message = "Groups should not be empty")
    private List<NameValuePayload<Long>> groups;
    @NotNull(message = "LibraryTypePayload should not be null")
    private LibraryTypePayload libraryTypePayload;

    // MUSIC
    private String albumArtImagePattern;


}

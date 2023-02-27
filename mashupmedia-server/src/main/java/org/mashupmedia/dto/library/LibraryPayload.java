package org.mashupmedia.dto.library;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.mashupmedia.dto.share.NameValuePayload;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LibraryPayload {

    private long id;
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Path should not be empty")
    private String path; 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
    private boolean enabled;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSuccessfulScanOn;
    @NotEmpty(message = "Groups should not be empty")
    private List<NameValuePayload<Long>> groups;
    @NotNull(message = "LibraryTypePayload should not be null")
    private LibraryTypePayload libraryTypePayload;

    // MUSIC
    private String albumArtImagePattern;


}

package org.mashupmedia.dto.library;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String path; 
    private LocationTypePayload locationTypePayload;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;
    private boolean enabled;
    @NotNull(message = "LibraryTypePayload should not be null")
    private LibraryTypePayload libraryTypePayload;
    private List<LibraryShareUserPayload> librarySharePayloads; 
    private boolean privateAccess; 
}

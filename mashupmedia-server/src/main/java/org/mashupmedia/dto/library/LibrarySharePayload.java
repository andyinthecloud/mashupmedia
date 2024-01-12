package org.mashupmedia.dto.library;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LibrarySharePayload {
    @Positive(message = "Expecting a valid libraryId")
    private long libraryId;
    @NotBlank(message = "Expecting an email address")
    private String email;
}

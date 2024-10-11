package org.mashupmedia.dto.library;

import org.mashupmedia.eums.MashupMediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LibraryFilePayload {
    private String name;
    private boolean isFolder;
    private String path;
    private MashupMediaType mashupMediaType;
}

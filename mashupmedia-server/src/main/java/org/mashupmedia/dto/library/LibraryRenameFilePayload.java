package org.mashupmedia.dto.library;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LibraryRenameFilePayload {
    private long libraryId;
    private String path;    
    private String name;
}

package org.mashupmedia.dto.library;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LibraryFilesPayload {
    private List<BreadcrumbPayload> breadcrumbPayloads;
    private List<LibraryFilePayload> libraryFilePayloads;
}

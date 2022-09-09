package org.mashupmedia.dto.library;

import org.mashupmedia.dto.share.NameValuePayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LibraryNameValuePayload extends NameValuePayload<Long>{
    private LibraryTypePayload libraryTypePayload;
}

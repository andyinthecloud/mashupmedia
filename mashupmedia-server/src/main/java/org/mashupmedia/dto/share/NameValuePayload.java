package org.mashupmedia.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NameValuePayload<T> {
    private long id;
    private String name;
    private T value;
}

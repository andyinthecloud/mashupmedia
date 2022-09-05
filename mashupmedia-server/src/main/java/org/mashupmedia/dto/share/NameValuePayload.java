package org.mashupmedia.dto.share;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NameValuePayload<T> {
    @NotBlank
    private String name;
    private T value;
}

package org.mashupmedia.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ServerResponsePayload<T>{
    private T payload;
    private ErrorPayload errorPayload;
}

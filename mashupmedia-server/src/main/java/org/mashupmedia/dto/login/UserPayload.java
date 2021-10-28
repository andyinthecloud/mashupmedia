package org.mashupmedia.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPayload {
    private final String token;
    private final String username;
    private final String message;
}

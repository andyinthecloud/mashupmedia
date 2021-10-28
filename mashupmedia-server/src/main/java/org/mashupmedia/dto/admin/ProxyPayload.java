package org.mashupmedia.dto.admin;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProxyPayload {
    private final Boolean enabled;
    private final String url;
    private final String port;
    private final String username;
    private final String password;
}

package org.mashupmedia.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProxyPayload {
    private Boolean enabled;
    private String url;
    private String port;
    private String username;
    private String password;
}

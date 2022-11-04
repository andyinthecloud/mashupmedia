package org.mashupmedia.mapper;

import org.mashupmedia.dto.media.SecureTokenPayload;

public abstract class SecureMediaDomainMapper<D, P> implements DomainMapper<D, P> {

    public SecureTokenPayload<P> toDto(D domain, String secureToken) {
        return SecureTokenPayload
                .<P>builder()
                .secureToken(secureToken)
                .payload(toDto(domain))
                .build();
    }

}

package org.mashupmedia.mapper;

import org.mashupmedia.dto.media.MediaTokenPayload;

public abstract class SecureMediaDomainMapper<D, P> implements DomainMapper<D, P> {

    public MediaTokenPayload<P> toDto(D domain, String mediaToken) {
        return MediaTokenPayload
                .<P>builder()
                .mediaToken(mediaToken)
                .payload(toDto(domain))
                .build();
    }

}

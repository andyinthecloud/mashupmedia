package org.mashupmedia.mapper;

import org.mashupmedia.dto.media.SecureMediaPayload;

public abstract class SecureMediaDomainMapper<D, P> implements DomainMapper<D, P> {

    public SecureMediaPayload<P> toDto(D domain, String mediaToken) {
        return SecureMediaPayload
                .<P>builder()
                .mediaToken(mediaToken)
                .payload(toPayload(domain))
                .build();
    }

    @Override
    public final D toDomain(P payload) {
        // Not required
        return null;
    }

}

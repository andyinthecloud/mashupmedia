package org.mashupmedia.mapper;

public interface DomainMapper<D, P> {
     P toPayload(D domain);
     D toDomain(P payload);
}

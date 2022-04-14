package org.mashupmedia.mapper;

public interface DomainMapper<D, P> {
     P toDto(D domain);
     D toDomain(P payload);
}

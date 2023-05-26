package org.mashupmedia.mapper;

public interface PayloadMapper<D, P> {
    P toPayload(D domain);
}

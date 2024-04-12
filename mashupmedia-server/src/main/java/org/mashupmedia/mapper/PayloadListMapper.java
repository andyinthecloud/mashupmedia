package org.mashupmedia.mapper;

public interface PayloadListMapper<D, P> {
    P toPayloadList(D domain);
}

package org.mashupmedia.mapper;

import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper implements DomainMapper<Group, NameValuePayload<Long>> {

    @Override
    public NameValuePayload<Long> toDto(Group domain) {
        return NameValuePayload
                .<Long>builder()
                .name(domain.getName())
                .value(domain.getId())
                .build();
    }

    @Override
    public Group toDomain(NameValuePayload<Long> payload) {
        return Group.builder()
                .id(payload.getValue())
                .name(payload.getName())
                .build();
    }

}

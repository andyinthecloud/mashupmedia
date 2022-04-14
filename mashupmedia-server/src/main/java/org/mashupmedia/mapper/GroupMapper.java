package org.mashupmedia.mapper;

import org.mashupmedia.dto.admin.GroupPayload;
import org.mashupmedia.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper implements DomainMapper<Group, GroupPayload> {

    @Override
    public GroupPayload toDto(Group domain) {
        return GroupPayload
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    @Override
    public Group toDomain(GroupPayload payload) {
        return Group.builder()
        .id(payload.getId())
        .name(payload.getName())
        .build();
    }

}

package org.mashupmedia.mapper;

import org.mashupmedia.dto.admin.RolePayload;
import org.mashupmedia.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements DomainMapper<Role, RolePayload> {

    @Override
    public RolePayload toDto(Role domain) {
        return RolePayload
                .builder()
                .idName(domain.getIdName())
                .name(domain.getName())
                .build();
    }

    @Override
    public Role toDomain(RolePayload payload) {
        return Role.builder()
                .idName(payload.getIdName())
                .name(payload.getName())
                .build();
    }

}

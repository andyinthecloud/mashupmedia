package org.mashupmedia.mapper;

import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.model.account.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements DomainMapper<Role, NameValuePayload<String>> {

    @Override
    public NameValuePayload<String> toPayload(Role domain) {
        return NameValuePayload
                .<String>builder()
                .name(domain.getName())
                .value(domain.getIdName())
                .build();
    }

    @Override
    public Role toDomain(NameValuePayload<String> payload) {
        return Role.builder()
                .idName(payload.getValue())
                .name(payload.getName())
                .build();
    }

}

package org.mashupmedia.mapper;

import org.mashupmedia.dto.admin.CreateUserPayload;
import org.mashupmedia.model.account.User;
import org.springframework.stereotype.Component;

@Component
public class CreateUserMapper implements DomainMapper<User, CreateUserPayload>{

    @Override
    public CreateUserPayload toPayload(User domain) {
        throw new UnsupportedOperationException("Unimplemented method 'toDto'");
    }

    @Override
    public User toDomain(CreateUserPayload payload) {
        return User.builder()
        .name(payload.getName())
        .username(payload.getUsername())
        .password(payload.getPassword())
        .build();
    }
    
}

package org.mashupmedia.mapper;

import org.mashupmedia.dto.login.UserPolicyPayload;
import org.mashupmedia.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserPolicyMapper implements DomainMapper<User, UserPolicyPayload> {

    @Override
    public UserPolicyPayload toPayload(User domain) {
        return UserPolicyPayload.builder()
                .name(domain.getName())
                .username(domain.getUsername())
                .administrator(domain.isAdministrator())
                .build();
    }

    @Override
    public User toDomain(UserPolicyPayload payload) {
        return User.builder()
                .name(payload.getName())
                .username(payload.getUsername())
                .build();
    }

}

package org.mashupmedia.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements DomainMapper<User, UserPayload> {

        @Autowired
        private RoleMapper roleMapper;

        @Override
        public UserPayload toPayload(User domain) {

                UserPayload userPayload = UserPayload.builder()
                                .name(domain.getName())
                                .username(domain.getUsername())
                                .enabled(domain.isEnabled())
                                .administrator(domain.isAdministrator())
                                .createdOn(DateHelper.toLocalDateTime(domain.getCreatedOn()))
                                .updatedOn(DateHelper.toLocalDateTime(domain.getUpdatedOn()))
                                .exists(domain.getId() > 0 ? true : false)
                                .validated(domain.isValidated())
                                .build();

                if (!AdminHelper.isAdministrator()) {
                        return userPayload;
                }

                List<NameValuePayload<String>> rolePayloads = domain.getRoles().stream()
                                .map(roleMapper::toPayload)
                                .collect(Collectors.toList());

                return userPayload.toBuilder()
                                .system(domain.isSystem())
                                .rolePayloads(rolePayloads)
                                .build();
        }

        @Override
        public User toDomain(UserPayload payload) {

                User user = User.builder()
                                .username(payload.getUsername())
                                .name(payload.getName())
                                .enabled(payload.isEnabled())
                                .build();

                if (!payload.isExists()) {
                        user = user.toBuilder()
                                        .password(payload.getPassword())
                                        .build();
                }

                if (!AdminHelper.isAdministrator()) {
                        return user;
                }

                Set<Role> roles = new HashSet<>();
                if (payload.getRolePayloads() != null) {
                        roles = payload.getRolePayloads().stream()
                                        .map(roleMapper::toDomain)
                                        .collect(Collectors.toSet());
                }

                return user.toBuilder()
                                .roles(roles)
                                .build();

        }

}

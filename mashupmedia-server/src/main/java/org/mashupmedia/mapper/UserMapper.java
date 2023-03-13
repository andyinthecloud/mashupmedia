package org.mashupmedia.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements DomainMapper<User, UserPayload> {

        @Autowired
        private GroupMapper groupMapper;

        @Autowired
        private RoleMapper roleMapper;

        @Override
        public UserPayload toDto(User domain) {

                UserPayload userPayload = UserPayload.builder()
                                .name(domain.getName())
                                .username(domain.getUsername())
                                .password(domain.getPassword())
                                .editable(domain.isEditable())
                                .enabled(domain.isEnabled())
                                .administrator(domain.isAdministrator())
                                .createdOn(DateHelper.toLocalDateTime(domain.getCreatedOn()))
                                .updatedOn(DateHelper.toLocalDateTime(domain.getUpdatedOn()))
                                .exists(domain.getId() > 0 ? true : false)
                                .build();

                if (!AdminHelper.isAdministrator()) {
                        return userPayload;
                }

                List<NameValuePayload<Long>> groupPayloads = domain.getGroups().stream()
                                .map(groupMapper::toDto)
                                .collect(Collectors.toList());

                List<NameValuePayload<String>> rolePayloads = domain.getRoles().stream()
                                .map(roleMapper::toDto)
                                .collect(Collectors.toList());

                return userPayload.toBuilder()
                                .system(domain.isSystem())
                                .groupPayloads(groupPayloads)
                                .rolePayloads(rolePayloads)
                                .build();
        }

        @Override
        public User toDomain(UserPayload payload) {

                User user = User.builder()
                                .username(payload.getUsername())
                                .name(payload.getName())
                                .enabled(payload.isEnabled())
                                .editable(payload.isEditable())
                                .build();

                if (!AdminHelper.isAdministrator()) {
                        return user;
                }

                Set<Group> groups = new HashSet<>();
                if (payload.getGroupPayloads() != null) {
                        groups = payload.getGroupPayloads().stream()
                                        .map(groupMapper::toDomain)
                                        .collect(Collectors.toSet());
                }

                Set<Role> roles = new HashSet<>();
                if (payload.getRolePayloads() != null) {
                        roles = payload.getRolePayloads().stream()
                                        .map(roleMapper::toDomain)
                                        .collect(Collectors.toSet());
                }

                return User.builder()
                                .username(payload.getUsername())
                                .name(payload.getName())
                                .enabled(payload.isEnabled())
                                .editable(payload.isEditable())
                                .system(payload.isSystem())
                                .groups(groups)
                                .roles(roles)
                                .build();

        }

}

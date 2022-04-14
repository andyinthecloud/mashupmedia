package org.mashupmedia.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.dto.admin.GroupPayload;
import org.mashupmedia.dto.admin.RolePayload;
import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
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

        List<GroupPayload> groupPayloads = domain.getGroups().stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());

        List<RolePayload> rolePayloads = domain.getRoles().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());

        return UserPayload.builder()
                .name(domain.getName())
                .username(domain.getUsername())
                .editable(domain.isEditable())
                .enabled(domain.isEnabled())
                .system(domain.isSystem())
                .groupPayloads(groupPayloads)
                .rolePayloads(rolePayloads)
                .createdOn(DateHelper.toLocalDateTime(domain.getCreatedOn()))
                .updatedOn(DateHelper.toLocalDateTime(domain.getUpdatedOn()))
                .build();
    }

    @Override
    public User toDomain(UserPayload payload) {

        Set<Group> groups = payload.getGroupPayloads().stream()
                .map(groupMapper::toDomain)
                .collect(Collectors.toSet());

        Set<Role> roles = payload.getRolePayloads().stream()
                .map(roleMapper::toDomain)
                .collect(Collectors.toSet());

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

package org.mashupmedia.controller.rest.meta;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.admin.GroupPayload;
import org.mashupmedia.dto.admin.RolePayload;
import org.mashupmedia.mapper.GroupMapper;
import org.mashupmedia.mapper.RoleMapper;
import org.mashupmedia.service.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    @Autowired
    private AdminManager adminManager;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RoleMapper roleMapper;

    @GetMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupPayload> getGroups() {
        return adminManager.getGroups()
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RolePayload> getRoles() {
        return adminManager.getRoles()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

}

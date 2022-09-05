package org.mashupmedia.controller.rest.meta;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.GroupMapper;
import org.mashupmedia.mapper.RoleMapper;
import org.mashupmedia.model.Group;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public List<NameValuePayload<Long>> getGroups() {
        return adminManager.getGroups()
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NameValuePayload<String>> getRoles() {
        return adminManager.getRoles()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/group/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NameValuePayload<Long>> getGroup(@PathVariable Long groupId) {
        Assert.notNull(groupId, "Expecting a value for idName");
        Group group = adminManager.getGroup(groupId);
        return ResponseEntity.ok().body(groupMapper.toDto(group));
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping(value = "/group", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> saveGroup(
            @Valid @RequestBody NameValuePayload<Long> nameValuePayload, Errors errors) {

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        Group group = null;
        long groupId = nameValuePayload.getValue();
        if (groupId > 0) {
            group = adminManager.getGroup(groupId);
            group.setName(nameValuePayload.getName());
        } else {
            group = groupMapper.toDomain(nameValuePayload);
        }

        if (groupId == 0) {

            final String groupName = group.getName();
            boolean duplicateName = adminManager.getGroups()
                    .stream()
                    .map(g -> g.getName())
                    .anyMatch(n -> n.equals(groupName));

            if (duplicateName) {
                errors.rejectValue("name", ErrorCode.NOT_UNIQUE.getErrorCode(), "The group name should be unique");
                return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE,
                        errors);
            }
        }

        adminManager.saveGroup(group);
        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping(value = "/group", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUser(@RequestBody long groupId) {
        if (groupId == 0) {
            return ResponseEntity.badRequest().body(false);
        }

        adminManager.deleteGroup(groupId);

        return ResponseEntity.ok().body(true);

    }

}

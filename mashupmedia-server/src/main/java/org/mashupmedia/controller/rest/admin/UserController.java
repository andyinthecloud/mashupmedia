package org.mashupmedia.controller.rest.admin;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.admin.ChangeUserPasswordPayload;
import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.dto.share.ErrorPayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.UserMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminManager adminManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPayload> getUser() {
        User user = AdminHelper.getLoggedInUser();

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }
    
    @PutMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPayload> saveUser(@RequestBody UserPayload userPayload) {
        User user = userMapper.toDomain(userPayload);
        adminManager.saveUser(user);
        User savedUser = adminManager.getUser(user.getUsername());
        return ResponseEntity.ok(userMapper.toDto(savedUser));    
    }

    @PutMapping(value = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> changePassword(@Valid @RequestBody ChangeUserPasswordPayload changeUserPasswordPayload, Errors errors ) {
                
        String username = changeUserPasswordPayload.getUsername();
        User savedUser = StringUtils.isBlank(username) ? AdminHelper.getLoggedInUser() : adminManager.getUser(username);        


        if (!passwordEncoder.matches(changeUserPasswordPayload.getCurrentPassword(), savedUser.getPassword())) {
            errors.rejectValue("currentPassword", "The current password is incorrect");
        }


        if (!changeUserPasswordPayload.getNewPassword().equals(changeUserPasswordPayload.getConfirmPassword())) {
            errors.rejectValue("newPassword", "The new password and confirm password should be the same");
        }

        if (!errors.hasErrors()) {
            adminManager.updatePassword(savedUser.getUsername(), changeUserPasswordPayload.getNewPassword());
        }


        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_RESPONSE_MESSAGE, errors);

    }
    
}

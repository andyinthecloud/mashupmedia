package org.mashupmedia.controller.rest.admin;

import java.util.List;
import java.util.stream.Collectors;


import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.admin.ChangeUserPasswordPayload;
import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.dto.share.ErrorCode;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.UserMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminManager adminManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/account/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPayload> getAccount(@PathVariable String username) {
        User user = adminManager.getUser(username);
        return getUserPayload(user);
    }

    @GetMapping(value = "/my-account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPayload> getMyAccount() {
        User user = AdminHelper.getLoggedInUser();
        return getUserPayload(user);
    }

    private ResponseEntity<UserPayload> getUserPayload(User user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> saveAccount(@Valid @RequestBody UserPayload userPayload,
            Errors errors) {

        User loggedInUser = AdminHelper.getLoggedInUser();
        if (!AdminHelper.isAdministrator() && !userPayload.getUsername().equals(loggedInUser.getUsername())) {
            throw new SecurityException("Only an administrator can update another user account");
        }

        if (!isValidPassword(userPayload.isExists(), userPayload.getPassword(), userPayload.getRepeatPassword()) ) {            
            errors.rejectValue(
                "password",
                ErrorCode.NON_MATCHING_PASSWORDS.getErrorCode(),
                "The password and repeat password should be the same");  
        }

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        User user = userMapper.toDomain(userPayload);
        adminManager.saveUser(user);
        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    private boolean isValidPassword(boolean exists, String password, String repeatPassword) {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(repeatPassword)) {
            return false;
        }
        return password.equals(repeatPassword);

    }

    @PutMapping(value = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> changePassword(
            @Valid @RequestBody ChangeUserPasswordPayload changeUserPasswordPayload, Errors errors) {

        String username = changeUserPasswordPayload.getUsername();
        if (StringUtils.isNotEmpty(username) && !AdminHelper.isAdministrator()) {
            throw new SecurityException("Only an administrator can update a password in another account");
        }

        User savedUser = StringUtils.isBlank(username) ? AdminHelper.getLoggedInUser() : adminManager.getUser(username);

        if (!passwordEncoder.matches(changeUserPasswordPayload.getCurrentPassword(), savedUser.getPassword())) {
            errors.rejectValue(
                    "currentPassword",
                    ErrorCode.INCORRECT_PASSWORD.getErrorCode(),
                    "The current password is incorrect");
        }

        if (!changeUserPasswordPayload.getNewPassword().equals(changeUserPasswordPayload.getConfirmPassword())) {
            errors.rejectValue(
                    "newPassword",
                    ErrorCode.NON_MATCHING_PASSWORDS.getErrorCode(),
                    "The new password and confirm password should be the same");
        }

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        adminManager.updatePassword(savedUser.getUsername(), changeUserPasswordPayload.getNewPassword());

        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserPayload>> getUsers() {
        List<UserPayload> userPayloads = adminManager.getUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userPayloads);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping(value = "/delete-account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUser(@RequestBody String username) {
        if (StringUtils.isBlank(username)) {
            return ResponseEntity.badRequest().body(false);
        }

        User user = adminManager.getUser(username);
        if (user == null) {
            return ResponseEntity.badRequest().body(false);
        }

        adminManager.deleteUser(user.getId());
        return ResponseEntity.ok(true);
    }

}

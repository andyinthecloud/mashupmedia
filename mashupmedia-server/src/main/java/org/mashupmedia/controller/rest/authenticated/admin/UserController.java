package org.mashupmedia.controller.rest.authenticated.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.admin.CreateUserPayload;
import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.dto.share.ServerResponsePayload;
import org.mashupmedia.mapper.CreateUserMapper;
import org.mashupmedia.mapper.UserMapper;
import org.mashupmedia.model.account.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.EmailService;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.ValidationUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/admin/user")
public class UserController {

    private final UserMapper userMapper;
    private final CreateUserMapper createUserMapper;
    private final AdminManager adminManager;
    private final EmailService emailService;

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

        return ResponseEntity.ok(userMapper.toPayload(user));
    }

    @PutMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> saveAccount(@Valid @RequestBody UserPayload userPayload,
            Errors errors) {

        User loggedInUser = AdminHelper.getLoggedInUser();
        if (!AdminHelper.isAdministrator() && !userPayload.getUsername().equals(loggedInUser.getUsername())) {
            throw new SecurityException("Only an administrator can update another user account");
        }

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }

        User user = userMapper.toDomain(userPayload);
        adminManager.saveUser(user);
        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserPayload>> getUsers() {
        List<UserPayload> userPayloads = adminManager.getUsers().stream()
                .map(userMapper::toPayload)
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


    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponsePayload<String>> createAccount(@Valid @RequestBody CreateUserPayload userPayload,
    Errors errors) {

        if (errors.hasErrors()) {
            return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_ERROR_RESPONSE_MESSAGE, errors);
        }
        
        User user = createUserMapper.toDomain(userPayload);
        adminManager.saveUser(user);

        User loggedInUser = AdminHelper.getLoggedInUser();
        emailService.sendUserCreatedEmail(loggedInUser.getUsername(), user.getUsername());
        return ValidationUtil.createResponseEntityPayload(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE, errors);
    }

}

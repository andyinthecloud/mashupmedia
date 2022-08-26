package org.mashupmedia.controller.rest;

import javax.validation.Valid;

import org.mashupmedia.dto.login.LoginPayload;
import org.mashupmedia.dto.login.UserPolicyPayload;
import org.mashupmedia.dto.login.UserTokenPayload;
import org.mashupmedia.mapper.UserPolicyMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class LoginController {

    @Autowired
    private AdminManager adminManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserPolicyMapper userPolicyMapper;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenPayload> login(@RequestBody @Valid LoginPayload loginPayload) {
        User user = adminManager.getUser(loginPayload.getUsername());

        if (!passwordEncoder.matches(loginPayload.getPassword(), user.getPassword())) {
            throw new SecurityException("Invalid username / password combination");
        }

        String token = passwordEncoder.encode(loginPayload.getUsername());
        return ResponseEntity.ok(UserTokenPayload.builder().token(token).build());
    }


    @GetMapping(value = "/user-policy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPolicyPayload> userPolicy() {
        User user = AdminHelper.getLoggedInUser();
        Assert.notNull(user, "User should not be null");
        Assert.isTrue(user.isEnabled(), "User is enabled");
        return ResponseEntity.ok(userPolicyMapper.toDto(user));
    }
}

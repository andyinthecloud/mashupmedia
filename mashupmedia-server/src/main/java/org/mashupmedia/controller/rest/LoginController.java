package org.mashupmedia.controller.rest;

import javax.validation.Valid;

import org.mashupmedia.dto.login.LoginPayload;
import org.mashupmedia.dto.login.UserPolicyPayload;
import org.mashupmedia.dto.login.UserTokenPayload;
import org.mashupmedia.mapper.UserPolicyMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class LoginController {

    private final AdminManager adminManager;

    private final PasswordEncoder passwordEncoder;

    private final UserPolicyMapper userPolicyMapper;

    private final org.mashupmedia.service.SecurityManager securityManager;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenPayload> login(@RequestBody @Valid LoginPayload loginPayload) throws Exception{
        User user = adminManager.getUser(loginPayload.getUsername());

        if (!passwordEncoder.matches(loginPayload.getPassword(), user.getPassword())) {
            throw new SecurityException("Invalid username / password combination");
        }

        String username = loginPayload.getUsername();
        String token = passwordEncoder.encode(username);
        String streamingToken = securityManager.generateStreamingToken(username);

        return ResponseEntity.ok(
                UserTokenPayload
                        .builder()
                        .token(token)
                        .streamingToken(streamingToken)
                        .build());
    }

    @GetMapping(value = "/user-policy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPolicyPayload> userPolicy() {
        User user = AdminHelper.getLoggedInUser();
        Assert.notNull(user, "User should not be null");
        Assert.isTrue(user.isEnabled(), "User is enabled");
        return ResponseEntity.ok(userPolicyMapper.toDto(user));
    }
}

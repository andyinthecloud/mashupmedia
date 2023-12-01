package org.mashupmedia.controller.unauthenticated;

import org.mashupmedia.dto.login.LoginPayload;
import org.mashupmedia.dto.login.UserTokenPayload;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/login")
@RequiredArgsConstructor
public class LoginController {

    private final AdminManager adminManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenPayload> login(@RequestBody @Valid LoginPayload loginPayload) throws Exception {
        User user = adminManager.getUser(loginPayload.getUsername());

        if (!passwordEncoder.matches(loginPayload.getPassword(), user.getPassword())) {
            throw new SecurityException("Invalid username / password combination");
        }

        String username = loginPayload.getUsername();
        String token = passwordEncoder.encode(username);

        return ResponseEntity.ok(
                UserTokenPayload
                        .builder()
                        .token(token)
                        .build());
    }

    @GetMapping(value = "/is-logged-in", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> status() {
        return ResponseEntity.ok(
                AdminHelper.getLoggedInUser() != null ? true : false);
    }

}

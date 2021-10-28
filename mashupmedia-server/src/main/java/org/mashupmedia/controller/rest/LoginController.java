package org.mashupmedia.controller.rest;

import org.mashupmedia.dto.login.LoginPayload;
import org.mashupmedia.dto.login.UserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public UserPayload delete_login(@RequestBody LoginPayload loginPayload) {
        return UserPayload.builder()
                .token(this.passwordEncoder.encode("test"))
                .build();
    }


//    @CrossOrigin
    @PostMapping("/")
    public UserPayload login(@RequestBody LoginPayload loginPayload) {
        return UserPayload.builder()
                .token(this.passwordEncoder.encode("test"))
                .build();
    }
}

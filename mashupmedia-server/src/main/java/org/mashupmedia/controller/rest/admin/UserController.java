package org.mashupmedia.controller.rest.admin;

import org.mashupmedia.dto.admin.UserPayload;
import org.mashupmedia.mapper.UserMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPayload> getUser() {
        User user = AdminHelper.getLoggedInUser();
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        UserPayload userPayload = userMapper.toDto(user);
        return ResponseEntity.ok(userPayload);
    }
    
}

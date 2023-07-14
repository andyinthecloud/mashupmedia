package org.mashupmedia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {
    
    @GetMapping("/playlists/**")
        public String redirectPlaylists() {
        return "forward:/";
    }

}

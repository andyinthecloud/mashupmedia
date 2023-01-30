package org.mashupmedia.controller.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MashupMediaErrorController implements ErrorController{

    @RequestMapping("/error")
    public String handleError() {        
        return "forward:/";
    }
    
}

package com.thanlinardos.resource_server.controller.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping(value = {"", "/", "home"})
    public String displayHomePage(Authentication authentication) {
        if (authentication instanceof AbstractAuthenticationToken token) {
            log.info("Logged in with token {}", token);
        }
        return "home";
    }
}

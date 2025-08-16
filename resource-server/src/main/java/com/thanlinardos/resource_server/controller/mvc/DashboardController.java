package com.thanlinardos.resource_server.controller.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String displayDashboard() {
        return "dashboard";
    }

}
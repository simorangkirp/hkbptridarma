package church_player_agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/", "/dashboard", "/search", "/lyrics",
            "/library", "/stage", "/settings"
    })
    public String index() {
        return "forward:/index.html";
    }
}
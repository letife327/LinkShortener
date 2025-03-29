package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.response.UserResponse;
import az.texnoera.link_shortener.security.SecurityUtils;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/test")
@RestController
public class UserController {
    @GetMapping
    public String getUser() {
        return "Hello World";
    }
    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }
}

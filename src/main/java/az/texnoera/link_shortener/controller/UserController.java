package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.response.UserDetailsResponse;
import az.texnoera.link_shortener.response.UserResponse;
import az.texnoera.link_shortener.security.SecurityUtils;
import az.texnoera.link_shortener.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/{id}")
    public UserDetailsResponse getUser(@PathVariable("id") Integer id) {
          return userService.getUser(id);
    }

}

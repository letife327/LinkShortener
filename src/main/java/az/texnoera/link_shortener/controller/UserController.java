package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.request.ProfileEditRequest;
import az.texnoera.link_shortener.response.UrlResponse;
import az.texnoera.link_shortener.response.UserDetailsResponse;
import az.texnoera.link_shortener.response.UserResponse;
import az.texnoera.link_shortener.result.PageResult;
import az.texnoera.link_shortener.security.SecurityUtils;
import az.texnoera.link_shortener.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/users")
@RestController
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class UserController {
    private final UserService userService;
    @GetMapping("/{id}")
    public UserDetailsResponse getUser(@PathVariable("id") Integer id) {
          return userService.getUser(id);
    }
    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") Integer id, @RequestBody @Valid ProfileEditRequest request) {
        userService.updateUser(id,request);
    }
    @GetMapping("/url-list/{user-id}")
    public PageResult<UrlResponse> getUrlListForUser(@PathVariable("user-id") Integer userId,
                                                     @RequestParam(defaultValue = "10") Integer size,
                                                     @RequestParam(defaultValue = "0") Integer page) {
        return userService.getUrlListForUser(userId,size,page);
    }
}

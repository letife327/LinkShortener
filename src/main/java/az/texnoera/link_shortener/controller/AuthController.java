package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.request.*;
import az.texnoera.link_shortener.response.UserResponse;
import az.texnoera.link_shortener.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Validated
@RequestMapping("v1/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {
   private final UserService userService;
    @PostMapping("/register")
    public String register(@RequestBody @Valid UserRegisterRequest userRegisterRequest){
        return userService.register(userRegisterRequest);
    }
    @PostMapping("/verifying-code")
    public String verifyingCode(@RequestBody @Valid VerificationRequest otp) {
        return userService.verifyingCode(otp);
    }
    @PostMapping("/login")
    public UserResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody @Valid ForgetPasswordRequest forgetPasswordRequest) {
        return userService.sendOtp(forgetPasswordRequest);
    }
    @PostMapping("/check-otp")
    public boolean checkOtp(@RequestBody @Valid VerificationRequest otp) {
        return userService.checkOtp(otp);
    }
    @PostMapping("reset-password")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        return userService.resetPassword(request);
    }

}

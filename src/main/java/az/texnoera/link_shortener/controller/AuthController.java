package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.request.*;
import az.texnoera.link_shortener.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("v1/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {
   private final UserService userService;
    @PostMapping("/register")
    public String register(@RequestBody UserRegisterRequest userRegisterRequest){
        return userService.register(userRegisterRequest);
    }
    @PostMapping("/verifying-code")
    public String verifyingCode(@RequestBody VerificationRequest otp) {
        return userService.verifyingCode(otp);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        return userService.sendOtp(forgetPasswordRequest);
    }
    @PostMapping("/check-otp")
    public boolean checkOtp(@RequestBody VerificationRequest otp) {
        return userService.checkOtp(otp);
    }
    @PostMapping("reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request){
        return userService.resetPassword(request);
    }

}

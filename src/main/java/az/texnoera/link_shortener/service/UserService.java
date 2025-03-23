package az.texnoera.link_shortener.service;

import az.texnoera.link_shortener.request.*;
import az.texnoera.link_shortener.entity.Role;
import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.enums.Status;
import az.texnoera.link_shortener.repository.RoleRepository;
import az.texnoera.link_shortener.repository.UserRepository;
import az.texnoera.link_shortener.security.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
   private final JWTUtils jwtUtils;
    public String register(UserRegisterRequest userRegisterRequest) {
        User user = new User();
        Integer otp = generateOtp();
        user.setFullName(userRegisterRequest.getFullName());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        Role role = roleRepository.findByName("USER");
        Set roles = new HashSet();
        roles.add(role);
        user.setRoles(roles);
        user.setOtp(otp);
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        Thread thread = new Thread(() -> {
            sendOtpEmail(user.getEmail(), otp);
        });
        thread.start();
        return "Sizin təsdiq kodunuz email ünvanınıza göndərilmişdir.";
    }

    public Integer generateOtp() {
        SecureRandom random = new SecureRandom();
        Integer otp = 100000 + random.nextInt(900000);
        return otp;
    }

    public void sendOtpEmail(String toEmail, Integer otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("OTP Kod");
        message.setText("Sizin təsdiq kodunuz: " + otp);

        mailSender.send(message);
    }

    public String verifyingCode(VerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email not found"));
        if (!user.getOtp().equals(request.getOtp())) {
            return "User not verified";
        }
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return "User successfully verified";
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("Email not found"));
        if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())
                && user.getStatus() == Status.ACTIVE) {

           return  jwtUtils.generateJwtToken(user.getUsername(),
                   user.getRoles().stream().map(Role::getName).toList());
        }
        throw new RuntimeException("Invalid password or email");
    }

    public String sendOtp(ForgetPasswordRequest forgetPasswordRequest) {
        User user = userRepository.findByEmail(forgetPasswordRequest.getEmail()).orElseThrow(()-> new RuntimeException("Email not found"));
        int otp = generateOtp() ;
        user.setOtp(otp);
        user.setExpiryDate(LocalDateTime.now().plusMinutes(3));
        userRepository.save(user);
        Thread thread = new Thread(() -> {
            userRepository.findByEmail(forgetPasswordRequest.getEmail()).ifPresent(u -> {
                sendOtpEmail(u.getEmail(), otp);
            });
        });
        thread.start();
        return "OTP sent";
    }

    public boolean checkOtp(VerificationRequest otp) {
        User user = userRepository.findByEmail(otp.getEmail()).orElseThrow(() -> new RuntimeException("Email not found"));
        if(!user.getOtp().equals(otp.getOtp())) {
            throw new RuntimeException("Invalid otp");
        }
        if(LocalDateTime.now().isAfter(user.getExpiryDate())) {
            throw new RuntimeException("Expired otp");
        }
        user.setIsVerified(true);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return true;
    }

    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email not found"));
        if(!user.getIsVerified()){
            throw new RuntimeException("User is not verified");
        }
        if(!user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid otp");
        }

        user.setStatus(Status.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsVerified(false);
        userRepository.save(user);

        return "Password changed";
    }
}

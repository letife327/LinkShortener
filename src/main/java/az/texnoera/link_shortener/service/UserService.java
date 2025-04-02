package az.texnoera.link_shortener.service;

import az.texnoera.link_shortener.entity.Url;
import az.texnoera.link_shortener.enums.StatusCodeForException;
import az.texnoera.link_shortener.exception.BaseException;
import az.texnoera.link_shortener.mapper.UrlMapper;
import az.texnoera.link_shortener.repository.UrlRepository;
import az.texnoera.link_shortener.request.*;
import az.texnoera.link_shortener.entity.Role;
import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.enums.Status;
import az.texnoera.link_shortener.repository.RoleRepository;
import az.texnoera.link_shortener.repository.UserRepository;
import az.texnoera.link_shortener.response.UrlResponse;
import az.texnoera.link_shortener.response.UserDetailsResponse;
import az.texnoera.link_shortener.response.UserResponse;
import az.texnoera.link_shortener.result.PageResult;
import az.texnoera.link_shortener.security.JWTUtils;
import az.texnoera.link_shortener.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UrlRepository urlRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JWTUtils jwtUtils;
    @Value("${file.download-url}")
    private String downloadUrl;

    public String register(UserRegisterRequest userRegisterRequest) {
        User user = new User();
        Integer otp = generateOtp();
        user.setFullName(userRegisterRequest.getFullName());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        Role role = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setOtp(otp);
        user.setExpiryDate(LocalDateTime.now().plusMinutes(2));
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
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND,StatusCodeForException.USER_NOT_FOUND));
        if (!user.getOtp().equals(request.getOtp())) {
            throw new BaseException(HttpStatus.BAD_REQUEST,StatusCodeForException.INVALID_OTP);
        }
        if (LocalDateTime.now().isAfter(user.getExpiryDate())) {
            throw new BaseException(HttpStatus.BAD_REQUEST,StatusCodeForException.EXPIRED_OTP);
        }
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return "User successfully verified";
    }

    public UserResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND,StatusCodeForException.USER_NOT_FOUND));
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())
                && user.getStatus() == Status.ACTIVE) {
            String token = jwtUtils.generateJwtToken(user.getUsername(),
                    user.getRoles().stream().map(Role::getName).toList());

            return new UserResponse(user.getId(), token, user.getFullName(), user.getEmail());
        }
        throw new BaseException(HttpStatus.BAD_REQUEST,StatusCodeForException.INVALID_PASSWORD_OR_EMAIL);
    }

    public String sendOtp(ForgetPasswordRequest forgetPasswordRequest) {
        User user = userRepository.findByEmail(forgetPasswordRequest.getEmail()).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND,StatusCodeForException.USER_NOT_FOUND));
        int otp = generateOtp();
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
        if (!user.getOtp().equals(otp.getOtp())) {
            throw new BaseException(HttpStatus.NOT_FOUND, StatusCodeForException.INVALID_OTP);
        }
        if (LocalDateTime.now().isAfter(user.getExpiryDate())) {
            throw new BaseException(HttpStatus.BAD_REQUEST,StatusCodeForException.EXPIRED_OTP);
        }
        user.setIsVerified(true);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return true;
    }

    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email not found"));
        if (!user.getIsVerified()) {
            throw new BaseException(HttpStatus.BAD_REQUEST, StatusCodeForException.USER_IS_NOT_VERIFIED);
        }
        if (!user.getOtp().equals(request.getOtp())) {
            throw new BaseException(HttpStatus.NOT_FOUND, StatusCodeForException.USER_NOT_FOUND) ;
        }

        user.setStatus(Status.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsVerified(false);
        userRepository.save(user);

        return "Password changed";
    }

    public UserDetailsResponse getUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, StatusCodeForException.USER_NOT_FOUND) );
        return UserDetailsResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .photoUrl(downloadUrl + user.getProfilePhoto())
                .build();
    }

    public void updateUser(Integer id, ProfileEditRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, StatusCodeForException.USER_NOT_FOUND) );
        user.setFullName(request.getFullName());
        userRepository.save(user);
    }

    public PageResult<UrlResponse> getUrlListForUser(Integer userId, Integer size, Integer page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, StatusCodeForException.USER_NOT_FOUND) );
        Pageable pages = PageRequest.of(page, size);
        Page<Url> pageUrl = urlRepository.findAllUrlsByUserId(user.getId(), pages);
        List<UrlResponse> urlResponses = UrlMapper.entityToResponse(pageUrl);
        return new PageResult<>(urlResponses, page, size, pageUrl.getTotalPages());
    }
}
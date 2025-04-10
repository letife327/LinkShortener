package az.texnoera.link_shortener.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCodeForException {
    USER_NOT_FOUND(1004,"User Not Found"),
    USER_IS_NOT_VERIFIED(1005,"User Not Verified"),
    INVALID_OTP(1006,"Invalid OTP"),
    EXPIRED_OTP(1007,"Expired OTP"),
    INVALID_PASSWORD_OR_EMAIL(1008,"Invalid Password or Email"),
    URL_NOT_FOUND(1009,"URL Not Found"),
    USER_ALREADY_EXISTS(10011,"User Already Exists");
    private final int statusCode;
    private final String message;
}

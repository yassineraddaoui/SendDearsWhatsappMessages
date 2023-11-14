package com.senddearswhatsappmessages.Services.auth;

import com.senddearswhatsappmessages.Entites.ResetPassword;
import com.senddearswhatsappmessages.Entites.Token;
import com.senddearswhatsappmessages.Entites.User;
import com.senddearswhatsappmessages.Entites.enums.TokenType;
import com.senddearswhatsappmessages.Entites.enums.UserRole;
import com.senddearswhatsappmessages.Payload.request.*;
import com.senddearswhatsappmessages.Payload.response.LoginResponse;
import com.senddearswhatsappmessages.Payload.response.MessageResponse;
import com.senddearswhatsappmessages.Repos.ResetPasswordRepository;
import com.senddearswhatsappmessages.Repos.TokenRepository;
import com.senddearswhatsappmessages.Repos.UserRepository;
import com.senddearswhatsappmessages.Security.jwt.JwtService;
import com.senddearswhatsappmessages.mail.EmailSender;
import com.senddearswhatsappmessages.mail.Otp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final Otp otpCmp;
    private final EmailSender emailSenderCmp;
    private final ResetPasswordRepository resetPasswordRepository;

    @Value("${carpool_app.frontend.url}")
    private String frontUrl;

    public MessageResponse register(RegisterRequest request) {
        String otpCode = otpCmp.generateOtp();
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.valueOf(request.getRole()))
                .otp(otpCode)
                .otpGeneratedTime(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        emailSenderCmp.sendOtpVerification(savedUser.getEmail(), otpCode);
        return MessageResponse.builder()
                .message("Registration done, check your email to verify your account with the OTP code")
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow((RuntimeException::new));
        if (user.isVerified()) {
            String jwtToken = jwtService.generateToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return LoginResponse.builder()
                    .token(jwtToken)
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .message("Welcome to TEKUP-Carpool project")
                    .build();
        }
        return LoginResponse.builder()
                .message("Your account is not verified")
                .build();
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public MessageResponse verifyAccount(VerifyAccountRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(RuntimeException::new);
        LocalDateTime otpGeneratedTime = user.getOtpGeneratedTime();
        LocalDateTime currentTime = LocalDateTime.now();
        long secondsDifference = Duration.between(otpGeneratedTime, currentTime).getSeconds();
        boolean isNotExpired = secondsDifference < 60;

        if (user.getOtp().equals(request.getOtp())) {
            if (isNotExpired) {
                if (!user.isVerified()) {
                    user.setVerified(true);
                    userRepository.save(user);
                    return MessageResponse.builder()
                            .message("Your OTP has been successfully verified. You now have access to the platform")
                            .build();
                } else {
                    return MessageResponse.builder()
                            .message("Your account is already verified. You have access to the platform")
                            .build();
                }
            } else {
                return MessageResponse.builder()
                        .message("OTP Code expired. Please regenerate another OTP code")
                        .build();
            }
        } else {
            return MessageResponse.builder()
                    .message("Invalid OTP code")
                    .build();
        }
    }

    public MessageResponse regenerateOtp(RegenerateOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(RuntimeException::new);
        if (!user.isVerified()) {
            String otpCode = otpCmp.generateOtp();
            user.setOtp(otpCode);
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);

            emailSenderCmp.sendOtpVerification(request.getEmail(), otpCode);

            return MessageResponse.builder()
                    .message("A new OTP code has been generated and sent to your email")
                    .build();
        } else {
            return MessageResponse.builder()
                    .message("Your account is already verified. You have access to the platform")
                    .build();
        }
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String url = generateResetToken(user);
        emailSenderCmp.sendResetPassword(request.getEmail(), url);
        return MessageResponse.builder()
                .message("Password reset instructions have been sent to your email")
                .build();
    }

    //Token reset password is set to be valid for 30mns
    public String generateResetToken(User user) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expirationDateTime = currentDateTime.plusMinutes(30);
        ResetPassword resetToken = ResetPassword.builder()
                .token(uuid.toString())
                .expirationDate(expirationDateTime)
                .user(user)
                .build();

        ResetPassword token = resetPasswordRepository.save(resetToken);
        return frontUrl + "/reset-password/" + token.getToken();
    }

    public MessageResponse resetPassword(String token, ResetPasswordRequest request) {
        if (request.getNewPassword().equals(request.getConfirmationPassword())) {
            ResetPassword resetPassword = resetPasswordRepository.findByToken(token).orElseThrow();
            if (isResetPasswordTokenValid(resetPassword.getExpirationDate())) {
                User user = resetPassword.getUser();
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return MessageResponse.builder()
                        .message("Password has been changed")
                        .build();
            } else {
                return MessageResponse.builder()
                        .message("Something went wrong")
                        .build();
            }
        } else {
            return MessageResponse.builder()
                    .message("New Password and Password Confirmation do not match")
                    .build();
        }
    }

    public boolean isResetPasswordTokenValid(LocalDateTime expirationDate) {
        LocalDateTime currentDate = LocalDateTime.now();
        return expirationDate.isAfter(currentDate);
    }
}

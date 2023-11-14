package com.senddearswhatsappmessages.Services.auth;


import com.senddearswhatsappmessages.Payload.request.*;
import com.senddearswhatsappmessages.Payload.response.LoginResponse;
import com.senddearswhatsappmessages.Payload.response.MessageResponse;

public interface AuthenticationService {
    MessageResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    MessageResponse verifyAccount(VerifyAccountRequest request);

    MessageResponse regenerateOtp(RegenerateOtpRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);

    MessageResponse resetPassword(String token, ResetPasswordRequest request);
}

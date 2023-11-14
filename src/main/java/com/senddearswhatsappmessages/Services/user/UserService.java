package com.senddearswhatsappmessages.Services.user;

import com.senddearswhatsappmessages.Payload.request.ChangePasswordRequest;

import java.security.Principal;

public interface UserService {
    boolean seedInitialUsers();

    String changePassword(ChangePasswordRequest email, Principal connectedUser);
}

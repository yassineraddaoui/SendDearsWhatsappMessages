package com.senddearswhatsappmessages.Controllers.user;

import com.senddearswhatsappmessages.Payload.request.ChangePasswordRequest;
import com.senddearswhatsappmessages.Services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/seed")
    public ResponseEntity<Boolean> seedUsers() {
        return ResponseEntity.ok().body(service.seedInitialUsers());
    }

    @GetMapping("/protected")
    public ResponseEntity<String> needsToken() {
        return ResponseEntity.ok().body("entered a protected route with a bearer token");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> onlyAdmin() {
        return ResponseEntity.ok().body("protected route and for admin only");
    }

    @PatchMapping("/update/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        return ResponseEntity.ok().body(service.changePassword(request, connectedUser));
    }
}

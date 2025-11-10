package com.example.bank.api.rest;

import com.example.bank.domain.user.model.User;
import com.example.bank.domain.user.service.AuthService;
import com.example.bank.security.JwtTokenProvider;
import com.example.bank.security.dto.AuthRequest;
import com.example.bank.security.dto.AuthResponse;
import com.example.bank.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.bank.domain.user.service.AuthService;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok("User created with id=" + user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authService.authenticate(request);
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}


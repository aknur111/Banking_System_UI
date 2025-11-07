package com.example.bank.domain.user.service;

import com.example.bank.domain.user.model.Role;
import com.example.bank.domain.user.model.User;
import com.example.bank.domain.user.repository.RoleRepository;
import com.example.bank.domain.user.repository.UserRepository;
import com.example.bank.security.dto.AuthRequest;
import com.example.bank.security.dto.AuthResponse;
import com.example.bank.security.dto.RegisterRequest;
import com.example.bank.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("User already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        var authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        String token = jwtTokenProvider.generateToken(
                new org.springframework.security.authentication.
                        UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities)
        );

        return new AuthResponse(token);
    }
}

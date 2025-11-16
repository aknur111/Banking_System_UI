package com.example.bank.domain.user.service;

import com.example.bank.domain.account.service.AccountService;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import com.example.bank.domain.user.model.Role;
import com.example.bank.domain.user.model.User;
import com.example.bank.domain.user.repository.RoleRepository;
import com.example.bank.domain.user.repository.UserRepository;
import com.example.bank.security.dto.AuthRequest;
import com.example.bank.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found in DB"));

        CustomerProfile profile = CustomerProfile.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .createdAt(OffsetDateTime.now())
                .build();

        profile = customerProfileRepository.save(profile);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .enabled(true)
                .customer(profile)
                .roles(Set.of(userRole))
                .build();

        user = userRepository.save(user);

        accountService.createAccount(profile.getId(), "KZT");

        return user;
    }

    public Authentication authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}

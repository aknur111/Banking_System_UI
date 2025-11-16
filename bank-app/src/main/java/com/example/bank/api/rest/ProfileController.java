package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomerProfileRepository customerProfileRepository;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMe(
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        Long customerId = 1L;

        CustomerProfile profile = customerProfileRepository
                .findById(customerId)
                .orElse(null);

        ProfileResponse r = toResponse(profile, username);
        return ResponseEntity.ok(r);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMe(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        Long customerId = 1L;

        CustomerProfile profile = customerProfileRepository
                .findById(customerId)
                .orElseGet(() -> {
                    CustomerProfile p = new CustomerProfile();
                    return p;
                });

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());

        CustomerProfile saved = customerProfileRepository.save(profile);
        ProfileResponse r = toResponse(saved, username);
        return ResponseEntity.ok(r);
    }

    private ProfileResponse toResponse(CustomerProfile profile, String username) {
        ProfileResponse r = new ProfileResponse();
        r.setUsername(username);

        if (profile != null) {
            r.setId(profile.getId());
            r.setFullName(profile.getFullName());
            r.setPhone(profile.getPhone());
            r.setMemberSince(profile.getCreatedAt());
        }

        r.setRole("Administrator");
        r.setRiskProfile("Standard");
        return r;
    }

    @Data
    public static class UpdateProfileRequest {
        private String fullName;
        private String phone;
    }

    @Data
    public static class ProfileResponse {
        private Long id;
        private String fullName;
        private String phone;
        private String username;
        private String role;
        private String riskProfile;
        private OffsetDateTime memberSince;
        private String email;
    }
}

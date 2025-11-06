package com.example.bank.domain.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bank.domain.user.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
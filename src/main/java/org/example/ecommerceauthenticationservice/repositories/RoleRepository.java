package org.example.ecommerceauthenticationservice.repositories;

import org.example.ecommerceauthenticationservice.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRole(String role);
}

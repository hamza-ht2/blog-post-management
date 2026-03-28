package com.example.post_management.repositories;

import com.example.post_management.models.User;
import com.example.post_management.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);
    List<User> findUsersByEnabled(boolean enabled);
    List<User> findUsersByRole(Role role);
    List<User> findUsersByEnabledAndRole(boolean enabled, Role role);
    List<User> findUsersByCountry(String country);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsUserById(Long userId);
}

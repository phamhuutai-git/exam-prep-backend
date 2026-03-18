package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    //auth
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmailOrUsername(String email, String username);

    Optional<Users> findByEmail(String email);

    Users findUsersByEmail(String email);

    // ===== CREATE =====
    boolean existsByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);

    // ===== UPDATE  =====
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Integer id);

}
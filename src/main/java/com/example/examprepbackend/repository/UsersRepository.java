package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    //auth
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmailOrUsername(String email, String username);

    Optional<Users> findByEmail(String email);

    Users findUsersByEmail(String email);

    Long countByRoleAndClasses_Id(Role role, Integer classes_id);

    List<Users> findByRole(Role role);

    List<Users> findByRoleAndClasses_Id(Role role, Integer classesId);

    //user
}
package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer> {

	Users findByEmail(String email);

    Users findByUsername(String username);

}
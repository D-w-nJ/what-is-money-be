package com.example.demo.src.user;

import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity,Long> {
}

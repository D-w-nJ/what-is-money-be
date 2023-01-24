package com.example.demo.src.user;

import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    UserEntity findUserEntityById(Long id);

    //유저정보수정_아이디
    @Query("update UserEntity m set m.userId = :userId where m.id = :userIdx")
    void updateUserId(@Param("userId") String userId, @Param("userIdx") Long userIdx);
}


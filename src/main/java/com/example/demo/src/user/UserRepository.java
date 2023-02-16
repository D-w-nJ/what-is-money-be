package com.example.demo.src.user;

import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Modifying
    @Query("delete from RecordEntity r where r.user.id= :userIdx")
    void deleteRecord(@Param("userIdx")Long userIdx);

    @Modifying
    @Query("delete from CategoryEntity c where c.user_id.id= :userIdx")
    void deleteCategory(@Param("userIdx")Long userIdx);

    @Modifying
    @Query("delete from GoalEntity g where g.user_id.id= :userIdx")
    void deleteGoal(@Param("userIdx")Long userIdx);

    @Modifying
    @Query("delete from QuestionEntity q where q.user.id= :userIdx")
    void deleteQuestion(@Param("userIdx")Long userIdx);
//
    @Modifying
    @Query("delete from UserEntity u where u.id= :userIdx")
    void deleteUser(@Param("userIdx")Long userIdx);


    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    UserEntity findUserEntityById(Long id);

    //아이디중복확인
    //@Query(value = "select userId from UserEntity where userId = :userId")
    //String findBy


    //유저정보수정_아이디
//    @Query("update UserEntity m set m.userId = :newUserId where m.id = :userIdx")
//    void updateUserId(@Param("newUserId") String newUserId, @Param("userIdx") Long userIdx);
}


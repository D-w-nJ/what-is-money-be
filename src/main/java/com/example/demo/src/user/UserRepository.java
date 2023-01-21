package com.example.demo.src.user;

import com.example.demo.src.user.model.GetIdCheckRes;
import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findById(String id_str);

    List<UserEntity> findUserEntityById(Long id);

//    @Query("select new com.example.demo.src.user.model.GetIdCheckRes(m.id_str, m.duplicate) from UserEntity m where m.id_str = :id_str")
//    List<GetIdCheckRes> CheckById(@Param("id") String id_str);

//    @Query("select new com.example.demo.src.user.model.GetIdCheckRes(m.id_str, m.duplicate) from UserEntity m where m.id_str = :id_str") //
//    GetIdCheckRes existsByUserId(@Param("id") String id_str);

//    @Query("select new com.example.demo.src.user.model.GetIdCheckRes(m.id_str, m.duplicate) from UserEntity m where m.id_str = :id_str") //
    @Query("select u from UserEntity u where u.id_str = :id_str")
    boolean existsByUserId(String id_str);

}


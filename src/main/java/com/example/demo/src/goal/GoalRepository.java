package com.example.demo.src.goal;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GetGoalRes;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {

    // @Query("select m from GoalEntity m where m.user_id = :userEntity")
    // List<GetGoalRes> findGoalEntities(@Param("userEntity") UserEntity userEntity);

    @Query("select new com.example.demo.src.goal.model.GetGoalRes(m.id, m.image, m.goal_amount, m.amount, m.progress) from GoalEntity m where m.user_id.id = :userIdx")
    List<GetGoalRes> findGoalList(@Param("userIdx") Long userIdx);

    //  @Query("select m from GoalEntity m left join f")
}

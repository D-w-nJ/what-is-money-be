package com.example.demo.src.goal;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GetGoalRes;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {

    // @Query("select m from GoalEntity m where m.user_id = :userEntity")
    // List<GetGoalRes> findGoalEntities(@Param("userEntity") UserEntity userEntity);

    @Query("select new com.example.demo.src.goal.model.GetGoalRes(m.id, m.image, m.goal_amount, m.amount, m.progress, m.category_name, m.date) from GoalEntity m where m.user_id.id = :userIdx")
    List<GetGoalRes> findGoalList(@Param("userIdx") Long userIdx);

    @Query("select new com.example.demo.src.goal.model.GetGoalRes(m.id, m.image, m.goal_amount, m.amount, m.progress, m.category_name, m.date) from GoalEntity m where m.user_id.id = :userIdx and m.id = :goalIdx")
    GetGoalRes findGoal(@Param("userIdx") Long userIdx, @Param("goalIdx") Long goalIdx);

    //  @Query("select m from GoalEntity m left join f")

    @Query("select new com.example.demo.src.goal.model.GetGoalRes(m.id, m.image, m.goal_amount, m.amount, m.progress, m.category_name, m.date) from GoalEntity m where m.user_id.id = :userIdx order by m.id asc")
    List<GetGoalRes> findGoalListByAsc(@Param("userIdx") Long userIdx);

    @Query("select new com.example.demo.src.goal.model.GetGoalRes(m.id, m.image, m.goal_amount, m.amount, m.progress, m.category_name, m.date) from GoalEntity m where m.user_id.id = :userIdx order by m.id desc")
    List<GetGoalRes> findGoalListByDesc(@Param("userIdx") Long userIdx);

    @Modifying(clearAutomatically = true) // 카테고리, 목표금액, 초기금액, 사진
    @Query("update GoalEntity m set m.goal_amount = :goalAmount, m.init_amount = :initAmount, m.category_name = :category_name, m.user_id = :userEntity where m.id = :goalIdx")
    void updateGoal(@Param("goalAmount") int goalAmount, @Param("initAmount") int initAmount, @Param("category_name") String category_name, @Param("userEntity") UserEntity userEntity, @Param("goalIdx") Long goalIdx);

}

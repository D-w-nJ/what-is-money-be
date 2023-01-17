package com.example.demo.src.goal;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {

    @Query("select m from GoalEntity m where m.user_id = :userIdx")
    List<GoalEntity> findGoalEntityByUser_id(@Param("userIdx") Long userIdx);
}

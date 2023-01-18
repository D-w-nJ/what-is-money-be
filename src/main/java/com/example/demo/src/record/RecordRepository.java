package com.example.demo.src.record;

import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.record.model.RecordEntity;
import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity,Long> {
    List<RecordEntity> findAllByDateBetweenAndUserAndGoal(LocalDateTime start, LocalDateTime end, UserEntity user, GoalEntity goal);
}

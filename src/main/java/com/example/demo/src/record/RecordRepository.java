package com.example.demo.src.record;

import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.record.model.RecordEntity;
import com.example.demo.src.user.model.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
    List<RecordEntity> findAllByDateBetweenAndUserAndGoal(LocalDateTime start, LocalDateTime end, UserEntity user, GoalEntity goal, Sort sort);

    @Query(value = "select date from RecordEntity where user=?1 and goal=?2")
    List<LocalDateTime> findDateByUserAndGoal(UserEntity user, GoalEntity goal);

    @Modifying
    @Query("delete from RecordEntity m where m.goal = :goalEntity")
    void deleteRecordEntitiesByGoalEntity(GoalEntity goalEntity);
}

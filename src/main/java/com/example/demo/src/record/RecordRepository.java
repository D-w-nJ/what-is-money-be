package com.example.demo.src.record;

import com.example.demo.src.record.model.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<RecordEntity,Long> {
    Long CountByUserAndGoal(int userIdx, int goalIdx);
}

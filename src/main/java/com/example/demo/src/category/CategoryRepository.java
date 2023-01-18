package com.example.demo.src.category;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.record.model.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    CategoryEntity findByCategoryIdx(Long categoryIdx);
}

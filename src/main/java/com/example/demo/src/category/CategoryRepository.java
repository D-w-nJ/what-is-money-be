package com.example.demo.src.category;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.category.model.GetCategoryRes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByCategoryIdx(Long categoryIdx);

    @Query("select new com.example.demo.src.category.model.GetCategoryRes(m.categoryIdx, m.category_name, m.flag) "
            + "from CategoryEntity m where m.user_id.id = :userIdx and m.flag = :flag")
    List<GetCategoryRes> findCategoryList(@Param("userIdx") Long userIdx, @Param("flag") int flag);
}

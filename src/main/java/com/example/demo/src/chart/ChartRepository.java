package com.example.demo.src.chart;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.chart.model.GetChartRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChartRepository extends JpaRepository<CategoryEntity, Long> {
    @Query(value = "select category_name, sum(amount) as total_amount from category c join record r " +
            "on c.id = r.category_id where c.user_id = :userIdx and c.flag = 1 " +
            "and date_format(date, '%Y-%m') = date_format(now(), '%Y-%m')" +
            "group by category_name", nativeQuery = true)
    List<GetChartRes> getChartResList(@Param("userIdx") Long userIdx);

    @Query(value = "select category_name, sum(amount) as total_amount from category c join record r " +
            "on c.id = r.category_id where c.user_id = :userIdx and c.flag = 1 " +
            "and date_format(date, '%Y-%m') = date_format(str_to_date(:lastM, '%Y-%m'),'%Y-%m') " +
            "group by category_name", nativeQuery = true)
    List<GetChartRes> getChartListByLast(@Param("userIdx") Long userIdx, @Param("lastM") String lastM);
}

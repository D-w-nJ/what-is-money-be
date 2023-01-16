package com.example.demo.src.category.model;

import com.example.demo.src.goal.model.GoalEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cateegory")
@Data
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity {
    @Id // 테이블의 PK 와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // pk 생성값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long categoryIdx;

    @OneToMany(mappedBy = "category_id")
    private List<GoalEntity> goalEntities = new ArrayList<>();
}

/*
@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private CategoryEntity category_id; // 외래키 : 어떤 카테코리에 속하는가?
 */
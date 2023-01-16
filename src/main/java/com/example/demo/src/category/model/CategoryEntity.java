package com.example.demo.src.category.model;

import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.user.model.UserEntity;
import com.example.demo.src.record.model.RecordEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
public class CategoryEntity {
    @Id // 테이블의 PK 와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // pk 생성값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    @Column(name="id")
    private Long categoryIdx;

    private String name;
    private int flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity user_id;

    @OneToMany(mappedBy = "category_id")
    private List<GoalEntity> goalEntities = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<RecordEntity> recordEntities = new ArrayList<>();
}
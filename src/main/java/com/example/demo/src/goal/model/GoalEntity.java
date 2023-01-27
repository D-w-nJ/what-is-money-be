
package com.example.demo.src.goal.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.*;
import org.apache.catalina.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Data
public class GoalEntity {
    @Id // 테이블의 PK 와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // pk 생성값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long id;
    private String image; // 목표 이미지
    private int goal_amount; // 목표 금액
    private int amount; // 현재 금액
    private float progress; // 진행률 퍼센트
    private LocalDateTime date; // 생설 날짜

    // name : 맵핑할 외래키의 이름
    // referencedColumnName : 외래키가 참조하는 대상 태이블의 실제 pk 이름

    private String category_name;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "category_id",referencedColumnName = "id")
    //private CategoryEntity category_id; // 외래키 : 어떤 카테코리에 속하는가?
    private int init_amount; // 초기값 (촤초에 얼마나 돈을 들고있었는가?)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity user_id; // 외래키 : 어떤 유저의 목표인가?
}


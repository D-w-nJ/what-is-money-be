
package com.example.demo.src.goal.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.*;
import org.apache.catalina.User;

import javax.persistence.*;

@Entity
@Table(name = "goal")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Data
public class GoalEntity {
    @Id // 테이블의 PK 와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // pk 생성값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long id;

    // @Column 을 사용할 경우 객체의 필드와 테이블의 컬럼을 매핑한다.
    // 아래와 같이 @Column 을 사용할 경우 필드의 이름을 테이블 컬럼 이름으로 사용한다.
    private String image; // 목표 이미지
    private String goal_amount; // 목표 금액
    private int amount; // 현재 금액
    private float progress; // 진행률 퍼센트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryIdx",referencedColumnName = "id")
    private CategoryEntity category_id; // 외래키 : 어떤 카테코리에 속하는가?
    private int init_amount; // 초기값 (촤초에 얼마나 돈을 들고있었는가?)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity user_id; // 외래키 : 어떤 유저의 목표인가?
}

/*
@Entity //JPA가 사용하는 객체라는 뜻이다. 이 어노테이션이 있어야 JPA가 인식할 수 있다.
@Table(name = "user")
@Getter @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Data
public class UserEntity {

    @Id //테이블의 PK와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long id;

    //@Column을 사용할 경우 객체의 필드와 테이블의 컬럼을 매핑한다.
    //아래와 같이 @Column을 생략할 경우 필드의 이름을 테이블 컬럼 이름으로 사용한다.
    private String password;
    private String name;
    private String email;
    private boolean agree;
    private boolean alarm;
    private int status;
    private String image;

    // 회원가입 (entity->DTO)
    public PostUserRes toPostUserRes(String jwt) {
        return new PostUserRes(id,jwt);
    }

    // 로그인 (entity -> DTO)
    public PostLoginRes toPostLoginRes(String jwt) {
        return new PostLoginRes(id, jwt);
    }
}

 */
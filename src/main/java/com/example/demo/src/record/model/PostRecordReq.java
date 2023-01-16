package com.example.demo.src.record.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.*;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(email, password)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
public class PostRecordReq {

    Long userIdx;
    Long goalIdx;
    String date;
    boolean type;
    Long category;
    int amount;

    public RecordEntity toEntity(UserEntity user, GoalEntity goal, CategoryEntity category) {
        return RecordEntity.builder()
                .amount(amount)
                .flag(type)
                .user(user)
                .goal(goal)
                .category(category)
                .build();
    }
}

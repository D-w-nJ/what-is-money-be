package com.example.demo.src.goal.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MakeGoalReq {
    private String image; // 이미지
    private int goal_amount; // 목표 금액
    private int init_amount; // 초기 금액

    private String category_name; // 카테고리명

    // 요청으로 받은 Goal 객체를 entity화하여 저장하는 용도
    public GoalEntity toEntity(CategoryEntity categoryEntity){
        return GoalEntity.builder()
                .image(image)
                .goal_amount(goal_amount)
                .init_amount(init_amount)
                .amount(init_amount) // amount 필드는 초기값(init_amount)으로 설정되도록
                .category_name(category_name)
                .build();
    }

    public GoalEntity toEntity(String image, int goalAmount, int initAmount, String category_name, UserEntity userEntity) {
        return GoalEntity.builder()
                .image(image)
                .goal_amount(goalAmount)
                .init_amount(initAmount)
                .category_name(category_name)
                .user_id(userEntity)
                .build();
    }
}

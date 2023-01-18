package com.example.demo.src.goal.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyGoalReq {
    private String image;
    private int goal_amount;
    private int init_amount;
    private Long categoryIdx;

    public ModifyGoalReq(String image, int goal_amount, int init_amount, Long categoryIdx){
        this.image = image;
        this.goal_amount = goal_amount;
        this.init_amount = init_amount;
        this.categoryIdx = categoryIdx;
    }

    public GoalEntity toEntity(String image, int goalAmount, int initAmount, CategoryEntity categoryEntity, UserEntity userEntity) {
        return GoalEntity.builder()
                .image(image)
                .goal_amount(goalAmount)
                .init_amount(initAmount)
                .category_id(categoryEntity)
                .user_id(userEntity)
                .build();
    }
}

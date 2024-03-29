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
    private int goal_amount;
    private int init_amount;

    private String category_name;

    public ModifyGoalReq(int goal_amount, int init_amount, String category_name){
        this.goal_amount = goal_amount;
        this.init_amount = init_amount;
        this.category_name = category_name;
    }

//    public GoalEntity toEntity(String image, int goalAmount, int amount, String category_name, UserEntity userEntity) {
//        return GoalEntity.builder()
//                .image(image)
//                .goal_amount(goalAmount)
//                .amount(amount)
//                .category_name(category_name)
//                .user_id(userEntity)
//                .build();
//    }
}

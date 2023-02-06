package com.example.demo.src.goal.model;


import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetGoalRes {
    private Long id;
    private byte[] image;
    private int goal_amount;
    private int amount;
    private int init_amount;
    private float progress;
    private String category_name;
    private LocalDateTime date;
    // private UserEntity userEntity;

    // private Long userIdx;

    public GetGoalRes(Long id, byte[] image, int goal_amount, int amount, int init_amount, float progress, String category_name, LocalDateTime date) {
        this.id = id;
        this.image = image;
        this.goal_amount = goal_amount;
        this.amount = amount;
        this.init_amount = init_amount;
        this.progress = ((float) amount / goal_amount) * 100;
        this.category_name = category_name;
        this.date = date;
        // this.userIdx = userIdx;
    }
}

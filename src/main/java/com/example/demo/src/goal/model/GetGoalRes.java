package com.example.demo.src.goal.model;


import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetGoalRes {
    private Long id;
    private String image;
    private int goal_amount;
    private int amount;
    private float progress;
    // private UserEntity userEntity;

    public GetGoalRes(Long id, String image, int goal_amount, int amount, float progress){
        this.id = id;
        this.image = image;
        this.goal_amount = goal_amount;
        this.amount = amount;
        this.progress = progress;
    }
}

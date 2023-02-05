package com.example.demo.src.goal.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageGoalReq {
    private MultipartFile image;
    public GoalEntity toEntity(String goalImgUrl){
        return GoalEntity.builder()
                .image(goalImgUrl)
                .build();
    }
}

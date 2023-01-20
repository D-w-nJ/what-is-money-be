package com.example.demo.src.category.model;

import com.example.demo.src.user.model.UserEntity;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCategoryReq {

    Long userIdx;
    String category_name;
    int flag;

    public CategoryEntity toEntity(UserEntity user, String category_name, int flag){
        return CategoryEntity.builder()
                .user_id(user)
                .category_name(category_name)
                .flag(flag)
                .build();
    }
}
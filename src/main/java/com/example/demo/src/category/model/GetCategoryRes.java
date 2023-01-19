package com.example.demo.src.category.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCategoryRes {

    private Long categoryIdx;
    private String category_name;
    private int flag;

    public GetCategoryRes(Long categoryIdx, String category_name, int flag) {
        this.categoryIdx = categoryIdx;
        this.category_name = category_name;
        this.flag = flag;
    }
}

package com.example.demo.src.category;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.model.GetCategoryRes;
import com.example.demo.utils.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final CategoryService categoryService;

    public CategoryController(JwtService jwtService, CategoryService categoryService) {
        this.jwtService = jwtService;
        this.categoryService = categoryService;
    }

    @GetMapping("{userIdx}/{flag}")
    public BaseResponse<List<GetCategoryRes>> getCategoryList(@PathVariable("userIdx") Long userIdx,
                                                              @PathVariable("flag") int flag) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GetCategoryRes> getCategoryResList = categoryService.getCategoryResList(userIdx, flag);
            return new BaseResponse<>(getCategoryResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}

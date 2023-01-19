package com.example.demo.src.category;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.model.GetCategoryRes;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<GetCategoryRes> getCategoryResList(Long userIdx, int flag) throws BaseException {
        try {
            List<GetCategoryRes> getCategoryResList = categoryRepository.findCategoryList(userIdx, flag);
            return getCategoryResList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

}

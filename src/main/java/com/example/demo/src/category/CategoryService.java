package com.example.demo.src.category;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.category.model.GetCategoryRes;
import com.example.demo.src.category.model.PostCategoryReq;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.model.UserEntity;

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
    private final UserRepository userRepository;

    @Transactional
    public List<GetCategoryRes> getCategoryResList(Long userIdx, int flag) throws BaseException {
        try {
            List<GetCategoryRes> getCategoryResList = categoryRepository.findCategoryList(userIdx, flag);
            return getCategoryResList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    @Transactional
    public void createCategory(PostCategoryReq postCategoryReq) throws BaseException {
        try {
            UserEntity userEntity = userRepository.findById(postCategoryReq.getUserIdx()).get();
            String category_name = postCategoryReq.getCategory_name();
            int flag = postCategoryReq.getFlag();
            CategoryEntity categoryEntity = postCategoryReq.toEntity(userEntity, category_name, flag);
            categoryRepository.save(categoryEntity);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Integer checkCategoryNameDuplication(PostCategoryReq postCategoryReq) throws IllegalStateException{
        Long  userIdx = postCategoryReq.getUserIdx();
        String category_name = postCategoryReq.getCategory_name();
        int flag = postCategoryReq.getFlag();
        Integer categoryNameDuplicate = categoryRepository.existsByCategory_name(userIdx, flag, category_name);
        return categoryNameDuplicate;
    }
}

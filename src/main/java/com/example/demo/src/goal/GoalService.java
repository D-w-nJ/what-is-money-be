package com.example.demo.src.goal;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CategoryRepository;
import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.goal.model.MakeGoalReq;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    @Autowired
    private final GoalRepository goalRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final JwtService jwtService;

    public void createGoal(MakeGoalReq makeGoalReq, Long category_id) throws BaseException{
        try{
            // Optional<CategoryEntity> categoryEntity = categoryRepository.findById(category_id);
            CategoryEntity categoryEntity = categoryRepository.findById(category_id).get();
            System.out.println("=====qweqw=eqw=eqw=e===");

            String image = makeGoalReq.getImage();
            int goal_amount = makeGoalReq.getGoal_amount();
            int init_amount = makeGoalReq.getInit_amount();
            GoalEntity goalEntity = makeGoalReq.toEntity(image, goal_amount, init_amount, categoryEntity);
            goalRepository.save(goalEntity);  // Goal 엔티티 저장
        } catch (Exception exception){
            System.out.println("=========================================");
            System.out.println(exception.getMessage());
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }
    /*
    public PostRecordRes createRecord(PostRecordReq postRecordReq) throws BaseException {
        try {
            RecordEntity record = postRecordReq.toEntity(
                    userRepository.findById(postRecordReq.getUserIdx()).orElse(null),
                    goalRepository.findById(postRecordReq.getGoalIdx()).orElse(null),
                    categoryRepository.findById(postRecordReq.getCategory()).orElse(null));
            recordRepository.save(record);
            return record.toPostRecordRes();
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
     */
}

package com.example.demo.src.goal;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.goal.model.MakeGoalReq;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    @Autowired
    private final GoalRepository goalRepository;
    @Autowired
    private final JwtService jwtService;

    public void createGoal(MakeGoalReq makeGoalReq, int category_id) throws BaseException{
        try{
            CategoryEntity categoryEntity = goalRepository.findByCategory_id(category_id);
            GoalEntity goalEntity = makeGoalReq.toEntity(categoryEntity);
            goalRepository.save(goalEntity);  // Goal 엔티티 저장
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }
}

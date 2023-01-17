package com.example.demo.src.goal;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CategoryRepository;
import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GetGoalRes;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.goal.model.MakeGoalReq;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.model.UserEntity;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {

    @Autowired
    private final GoalRepository goalRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Transactional
    public void createGoal(MakeGoalReq makeGoalReq, Long categoryIdx, Long userIdx) throws BaseException{
        try{
            // Optional<CategoryEntity> categoryEntity = categoryRepository.findById(category_id);
            // CategoryEntity categoryEntity = categoryRepository.findById(categoryIdx).get();
            CategoryEntity categoryEntity = categoryRepository.findByCategoryIdx(categoryIdx);
            UserEntity userEntity = userRepository.findById(userIdx).get();

            String image = makeGoalReq.getImage();
            int goal_amount = makeGoalReq.getGoal_amount();
            int init_amount = makeGoalReq.getInit_amount();
            GoalEntity goalEntity = makeGoalReq.toEntity(image, goal_amount, init_amount, categoryEntity, userEntity);
            goalRepository.save(goalEntity);  // Goal 엔티티 저장
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }


    public List<GoalEntity> getGoalResList(Long userIdx) throws BaseException{
        try{
            System.out.println("==============");
            System.out.println(userIdx);
            UserEntity userEntity = userRepository.findUserEntityById(userIdx);
            System.out.println(userEntity);
            System.out.println("=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            System.out.println("=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            System.out.println("=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            System.out.println(userEntity);
            System.out.println("=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            System.out.println("=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            List<GoalEntity> getGoalResList = goalRepository.findGoalEntities(userEntity);
            System.out.println("2222=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            System.out.println("2222=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");
            System.out.println("2222=============================ojqiwejioqwejioqwjeioqwjioejwqoieqwe");

            return getGoalResList;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

}

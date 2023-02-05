package com.example.demo.src.goal;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CategoryRepository;
import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.*;
import com.example.demo.src.record.RecordRepository;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.model.UserEntity;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private final RecordRepository recordRepository;

    @Value("${spring.file.path}")
    private String uploadFolder;

    @Transactional
    public void createGoal(MakeGoalReq makeGoalReq, Long userIdx) throws BaseException {
        try {
            // Optional<CategoryEntity> categoryEntity = categoryRepository.findById(category_id);
            // CategoryEntity categoryEntity = categoryRepository.findById(categoryIdx).get();

            // CategoryEntity categoryEntity = categoryRepository.findByCategoryIdx(categoryIdx);
            UserEntity userEntity = userRepository.findById(userIdx).get();
            // String image = makeGoalReq.getImage();
            int goal_amount = makeGoalReq.getGoal_amount();
            int init_amount = makeGoalReq.getInit_amount();
            String category_name = makeGoalReq.getCategory_name();

            GoalEntity goalEntity = makeGoalReq.toEntity(goal_amount, init_amount, category_name, userEntity);
            goalRepository.save(goalEntity);  // Goal 엔티티 저장
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }


    public List<GetGoalRes> getGoalResList(Long userIdx) throws BaseException {
        try {
            // UserEntity userEntity = userRepository.findById(userIdx).get();
            List<GetGoalRes> getGoalResList = goalRepository.findGoalList(userIdx);
            return getGoalResList;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteGoal(Long goalIdx) throws BaseException {
        try {
            GoalEntity goalEntity = goalRepository.findById(goalIdx).get();
            recordRepository.deleteRecordEntitiesByGoalEntity(goalEntity);
            goalRepository.deleteById(goalIdx);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    // 오름차순 정렬
    public List<GetGoalRes> getGoalResListAsc(Long userIdx) throws BaseException {
        try {
            // UserEntity userEntity = userRepository.findById(userIdx).get();
            List<GetGoalRes> getGoalResList = goalRepository.findGoalListByAsc(userIdx);
            return getGoalResList;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    public List<GetGoalRes> getGoalResListDesc(Long userIdx) throws BaseException {
        try {
            // UserEntity userEntity = userRepository.findById(userIdx).get();
            List<GetGoalRes> getGoalResList = goalRepository.findGoalListByDesc(userIdx);
            return getGoalResList;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    public GetGoalRes getGoalRes(Long userIdx, Long goalIdx) throws BaseException {
        try {
            // UserEntity userEntity = userRepository.findById(userIdx).get();
            GetGoalRes getGoalRes = goalRepository.findGoal(userIdx, goalIdx);
            return getGoalRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    @Transactional
    public void modifyGoal(Long goalIdx, Long userIdx, ModifyGoalReq modifyGoalReq) throws BaseException {
        try {
            int goalAmount = modifyGoalReq.getGoal_amount();
            int initAmount = modifyGoalReq.getInit_amount();
            // Long categoryIdx = modifyGoalReq.getCategoryIdx();
            String goalImage = modifyGoalReq.getImage();
            String category_name = modifyGoalReq.getCategory_name();
            GoalEntity goal = goalRepository.findGoalEntityById(goalIdx);
            int origialInitAmount = goal.getInit_amount();

            // CategoryEntity categoryEntity = categoryRepository.findByCategoryIdx(categoryIdx);
            UserEntity userEntity = userRepository.findById(userIdx).get();
            goalRepository.updateGoal(goalAmount, initAmount, category_name, userEntity, goalIdx, goalImage);
            goalRepository.updateAmount(goal.getAmount() - origialInitAmount + initAmount, goalIdx);

        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    @Transactional
    public void uploadGoalImage(Long goalIdx, Long userIdx, ImageGoalReq imageGoalReq) throws BaseException{
        try{
            GoalEntity goalEntity = goalRepository.findById(goalIdx).get();
            UUID uuid = UUID.randomUUID(); // 이미지의 고유성 보장
            String imageFileName = uuid+"_"+imageGoalReq.getImage().getOriginalFilename();
            Path imageFilePath = Paths.get(uploadFolder + "/" + imageFileName);
            try{
                Files.write(imageFilePath, imageGoalReq.getImage().getBytes());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            goalEntity.setImage(imageFileName); // image 설정
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }
    /*
    //회원정보설정_프로필사진
    public void postUserImage(PostUserImageReq postUserImageReq) throws BaseException{
        try{
            UserEntity userEntity = userRepository.findById(postUserImageReq.getUserIdx()).get();
            System.out.println("------userEntity??---------"+userEntity);
            UUID uuid = UUID.randomUUID(); //이미지 고유성 보장
            System.out.println("--------------uuid----------"+uuid);
            String imageFileName = uuid+"_"+postUserImageReq.getImage().getOriginalFilename();
            System.out.println("--------imageFileName??---------"+imageFileName);
            Path imageFilePath = Paths.get(uploadFolder + "/" + imageFileName);
            System.out.println("--------path??---------"+imageFilePath);

            try{
                Files.write(imageFilePath, postUserImageReq.getImage().getBytes());
            }catch(Exception e){
                e.printStackTrace();
            }

            userEntity.setImage(imageFileName);

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
     */
}



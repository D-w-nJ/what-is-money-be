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
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
            List<GetGoalRes> result = new ArrayList<GetGoalRes>(); // 결과물 리스트

            // UserEntity userEntity = userRepository.findById(userIdx).get();
            List<GetGoalMiddle> getGoalResList = goalRepository.findGoalList(userIdx);
            for (GetGoalMiddle getGoalMiddle : getGoalResList){
                String imageName = getGoalMiddle.getImage();
                if(imageName != null){
                    InputStream imageStream = new FileInputStream(uploadFolder + "/" + imageName);
                    byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                    imageStream.close();
                    // 여기까지 이미지 추출

                    Long id = getGoalMiddle.getId();
                    int goal_amount = getGoalMiddle.getGoal_amount();
                    int amount = getGoalMiddle.getAmount();
                    int init_amount = getGoalMiddle.getInit_amount();
                    float progress = amount / goal_amount;  // 진행률 계산
                    String category_name  = getGoalMiddle.getCategory_name();
                    LocalDateTime date = getGoalMiddle.getDate();

                    GetGoalRes getGoalRes = new GetGoalRes(id, imageByteArray, goal_amount, amount, init_amount, progress, category_name, date);
                    result.add(getGoalRes);
                }
                else {  // image 값이 null 인 경우
                    Long id = getGoalMiddle.getId();
                    int goal_amount = getGoalMiddle.getGoal_amount();
                    int amount = getGoalMiddle.getAmount();
                    int init_amount = getGoalMiddle.getInit_amount();
                    float progress = amount / goal_amount;  // 진행률 계산
                    String category_name  = getGoalMiddle.getCategory_name();
                    LocalDateTime date = getGoalMiddle.getDate();

                    GetGoalRes getGoalRes = new GetGoalRes(id, null, goal_amount, amount, init_amount, progress, category_name, date);
                    result.add(getGoalRes);
                }
            }
            return result;
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
            List<GetGoalRes> result = new ArrayList<GetGoalRes>(); // 결과물 리스트

            // UserEntity userEntity = userRepository.findById(userIdx).get();
            List<GetGoalMiddle> getGoalResList = goalRepository.findGoalListByAsc(userIdx);
            for (GetGoalMiddle getGoalMiddle : getGoalResList){
                String imageName = getGoalMiddle.getImage();
                if(imageName != null){
                    InputStream imageStream = new FileInputStream(uploadFolder + "/" + imageName);
                    byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                    imageStream.close();
                    // 여기까지 이미지 추출

                    Long id = getGoalMiddle.getId();
                    int goal_amount = getGoalMiddle.getGoal_amount();
                    int amount = getGoalMiddle.getAmount();
                    int init_amount = getGoalMiddle.getInit_amount();
                    float progress = amount / goal_amount;  // 진행률 계산
                    String category_name  = getGoalMiddle.getCategory_name();
                    LocalDateTime date = getGoalMiddle.getDate();

                    GetGoalRes getGoalRes = new GetGoalRes(id, imageByteArray, goal_amount, amount, init_amount, progress, category_name, date);
                    result.add(getGoalRes);
                }
                else {  // image 값이 null 인 경우
                    Long id = getGoalMiddle.getId();
                    int goal_amount = getGoalMiddle.getGoal_amount();
                    int amount = getGoalMiddle.getAmount();
                    int init_amount = getGoalMiddle.getInit_amount();
                    float progress = amount / goal_amount;  // 진행률 계산
                    String category_name  = getGoalMiddle.getCategory_name();
                    LocalDateTime date = getGoalMiddle.getDate();

                    GetGoalRes getGoalRes = new GetGoalRes(id, null, goal_amount, amount, init_amount, progress, category_name, date);
                    result.add(getGoalRes);
                }
            }
            return result;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }


    public List<GetGoalRes> getGoalResListDesc(Long userIdx) throws BaseException {
        try {
            List<GetGoalRes> result = new ArrayList<GetGoalRes>(); // 결과물 리스트

            // UserEntity userEntity = userRepository.findById(userIdx).get();
            List<GetGoalMiddle> getGoalResList = goalRepository.findGoalListByDesc(userIdx);
            for (GetGoalMiddle getGoalMiddle : getGoalResList){
                String imageName = getGoalMiddle.getImage();
                if(imageName != null){
                    InputStream imageStream = new FileInputStream(uploadFolder + "/" + imageName);
                    byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                    imageStream.close();
                    // 여기까지 이미지 추출

                    Long id = getGoalMiddle.getId();
                    int goal_amount = getGoalMiddle.getGoal_amount();
                    int amount = getGoalMiddle.getAmount();
                    int init_amount = getGoalMiddle.getInit_amount();
                    float progress = amount / goal_amount;  // 진행률 계산
                    String category_name  = getGoalMiddle.getCategory_name();
                    LocalDateTime date = getGoalMiddle.getDate();

                    GetGoalRes getGoalRes = new GetGoalRes(id, imageByteArray, goal_amount, amount, init_amount, progress, category_name, date);
                    result.add(getGoalRes);
                }
                else {  // image 값이 null 인 경우
                    Long id = getGoalMiddle.getId();
                    int goal_amount = getGoalMiddle.getGoal_amount();
                    int amount = getGoalMiddle.getAmount();
                    int init_amount = getGoalMiddle.getInit_amount();
                    float progress = amount / goal_amount;  // 진행률 계산
                    String category_name  = getGoalMiddle.getCategory_name();
                    LocalDateTime date = getGoalMiddle.getDate();

                    GetGoalRes getGoalRes = new GetGoalRes(id, null, goal_amount, amount, init_amount, progress, category_name, date);
                    result.add(getGoalRes);
                }
            }
            return result;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    public GetGoalRes getGoalRes(Long userIdx, Long goalIdx) throws BaseException {
        try {
            GetGoalRes getGoalRes;
            // UserEntity userEntity = userRepository.findById(userIdx).get();
            GetGoalMiddle getGoalMiddle = goalRepository.findGoal(userIdx, goalIdx);
            String imageName = getGoalMiddle.getImage();
            if(imageName == null) {
                Long id = getGoalMiddle.getId();
                int goal_amount = getGoalMiddle.getGoal_amount();
                int amount = getGoalMiddle.getAmount();
                int init_amount = getGoalMiddle.getInit_amount();
                float progress = amount / goal_amount;  // 진행률 계산
                String category_name = getGoalMiddle.getCategory_name();
                LocalDateTime date = getGoalMiddle.getDate();

                getGoalRes = new GetGoalRes(id, null, goal_amount, amount, init_amount, progress, category_name, date);
            }
            else {
                InputStream imageStream = new FileInputStream(uploadFolder + "/" + imageName);
                byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                imageStream.close();

                Long id = getGoalMiddle.getId();
                int goal_amount = getGoalMiddle.getGoal_amount();
                int amount = getGoalMiddle.getAmount();
                int init_amount = getGoalMiddle.getInit_amount();
                float progress = amount / goal_amount;  // 진행률 계산
                String category_name = getGoalMiddle.getCategory_name();
                LocalDateTime date = getGoalMiddle.getDate();

                getGoalRes = new GetGoalRes(id, imageByteArray, goal_amount, amount, init_amount, progress, category_name, date);
            }
            return getGoalRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }


    /*
    public GetUsersProfileRes getUsers(Long userIdx)throws BaseException{
        GetUsersProfileRes getUsersProfileRes;
        try{
            UserEntity user = userRepository.findById(userIdx).get();
            System.out.println("------------userEntity-----------"+user);
            String name = user.getName();
            String userId = user.getUserId();
            String imageName = user.getImage();
            System.out.println("image??"+imageName);

            if(imageName==null){
                getUsersProfileRes = new GetUsersProfileRes(name, userId, null);
            }else{
                //이미지를 불러오기
                //해당 경로의 image를 FileInputstream의 객체를 만들어서
                //byte[] 형태의 값으로 incoding 후 보내게 된다
                System.out.println("여기서???"+uploadFolder);
                InputStream imageStream = new FileInputStream(uploadFolder+"/"+imageName);
                System.out.println("=============imageStream????====="+imageStream);
                byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                imageStream.close();

                System.out.println("================imageByteArray???======"+imageByteArray);

                getUsersProfileRes = new GetUsersProfileRes(name, userId, imageByteArray);

            }

            return getUsersProfileRes;


        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
     */


    @Transactional
    public void modifyGoal(Long goalIdx, Long userIdx, ModifyGoalReq modifyGoalReq) throws BaseException {
        try {
            int goalAmount = modifyGoalReq.getGoal_amount();
            int initAmount = modifyGoalReq.getInit_amount();
            // Long categoryIdx = modifyGoalReq.getCategoryIdx();
            // String goalImage = modifyGoalReq.getImage();
            String category_name = modifyGoalReq.getCategory_name();
            GoalEntity goal = goalRepository.findGoalEntityById(goalIdx);
            int origialInitAmount = goal.getInit_amount();

            // CategoryEntity categoryEntity = categoryRepository.findByCategoryIdx(categoryIdx);
            UserEntity userEntity = userRepository.findById(userIdx).get();
            goalRepository.updateGoal(goalAmount, initAmount, category_name, userEntity, goalIdx);
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



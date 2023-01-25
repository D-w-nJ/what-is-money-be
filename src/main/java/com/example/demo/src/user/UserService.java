package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.question.model.PostQuestionReq;
import com.example.demo.src.record.model.RecordEntity;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    //회원가입
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
//        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
//            throw new BaseException(POST_USERS_EXISTS_EMAIL);
//        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword()); // 암호화코드
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            UserEntity userEntity = postUserReq.toEntity();   // DTO -> Entity 변환
            userRepository.save(userEntity);   // 유저 DB에 저장
            return userEntity.toPostUserRes();    // Entity -> DTO 변환
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //아이디중복확인
    public boolean CheckId(String userId)throws BaseException{
        boolean duplicate =  false;
        try{
            UserEntity chkID = userRepository.findByUserId(userId);
            if(chkID == null){
                return false;
            }
            System.out.println("chkID?? "+chkID);
            System.out.println("userId?? "+userId);
            if(chkID.getUserId().equals(userId)){
                System.out.println("이미 데베에 존재해~!!");
                duplicate =  true;
            }
            return duplicate;
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //로그인(password 검사)
    public PostLoginRes login(PostLoginReq postLoginReq)throws BaseException {
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postLoginReq.getPassword()); //암호화
            // 회원가입 할 때 비밀번호가 암호화되어 저장되었기 때문에 로그인 할 때도 암호화된 값끼리 비교를 해야함
        } catch (Exception e) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            UserEntity user = userRepository.findByUserId(postLoginReq.getUserId());
            user.setPassword(password);
            if (user.getPassword().equals(password)) { //비밀번호가 같다면
                String jwt = jwtService.createJwt(user.getId());
                return user.toPostLoginRes(jwt);
            } else {
                throw new BaseException(FAILED_TO_LOGIN);
            }
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    //로그아웃
    //    public PostLogoutRes logout(PostLogoutReq postLogoutReq)throws BaseException{
//        try{
//            // 2. Access Token 에서 User email  을 가져옵니다.
//            Authentication authentication = jwtService.getAuthentication(postLogoutReq.getAccessToken());
//
//            // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
//            if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
//                // Refresh Token 삭제
//                redisTemplate.delete("RT:" + authentication.getName());
//            }
//
//            // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
//            Long expiration = jwtService.getExpiration(postLogoutReq.getAccessToken());
//            redisTemplate.opsForValue()
//                    .set(postLogoutReq.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
//            throw new BaseException(SUCCESS);
//        } catch(Exception e){
//            throw new BaseException(SERVER_ERROR);
//        }
//
//    }

    //유저정보수정_아이디
    public void modifyUserId(PatchUserIdReq patchUserIdReq)throws BaseException{

        try{
            String newUserId = patchUserIdReq.getNewUserId();
            Long userIdx = patchUserIdReq.getUserIdx();

            UserEntity userEntity = userRepository.findById(userIdx).get();

            userEntity.updateUserId(newUserId);
            //System.out.println("userService이다. userRepository가기 전. 여기까진 되었나?!");

            //userRepository.updateUserId(newUserId, userIdx);

        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.MODIFY_FAIL_USERID);
        }
    }

    //유저정보수정_비밀번호
    public void modifyPassword(PatchPasswordReq patchPasswordReq) throws BaseException{
        String newPassword;
        try{

            Long userIdx = patchPasswordReq.getUserIdx();

            UserEntity userEntity = userRepository.findById(userIdx).get();


            try{
                // 암호화: patchPasswordReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
                newPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchPasswordReq.getNewPassword());
            } catch (Exception ignored){
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }
            userEntity.updatePassword(newPassword);
            //userRepository.updateUserId(newUserId, userIdx);

        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.MODIFY_FAIL_PASSWORD);
        }
    }
    //회원탈퇴
    public void deleteUser(DeleteUserReq deleteUserReq)throws BaseException{
        try{
            userRepository.deleteById(deleteUserReq.getUserIdx());
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //회원정보설정_프로필사진
    public void postUserImage(PostUserImageReq postUserImageReq) throws BaseException{
        try{
            UserEntity userEntity = userRepository.findById(postUserImageReq.getUserIdx()).get();
            String image = postUserImageReq.getImage();

            userEntity.saveImage(image);

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //회원정보설정_알림
    public void postUserAlarm(PostUserAlarmReq postUserAlarmReq)throws BaseException{
        try{
            UserEntity userEntity = userRepository.findById(postUserAlarmReq.getUserIdx()).get();
            boolean alarm = postUserAlarmReq.isAlarm();

            userEntity.saveAlarm(alarm);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //아이디찾기
    public String findUserId(FindUserIdReq findUserIdReq)throws BaseException{
        try{
            //해당 이메일을 가진 userEntity찾기
            UserEntity userEntity = userRepository.findByEmail(findUserIdReq.getEmail());
            //userEntity에서 userId 추출 후 FindUserIdRes로 반환
            String userId = userEntity.getUserId();

            return userId;

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }



    //비밀번호재설정
    public void resetPassword(ResetPassword resetPassword)throws BaseException{
        try{
            //해당 아이디를 가진 userEntity찾기
            UserEntity userEntity = userRepository.findByUserId(resetPassword.getUserId());

            //비밀번호 재설정
            String newPassword;
            try{
                try{
                    // 암호화: patchPasswordReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
                    newPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(resetPassword.getNewPassword());
                } catch (Exception ignored){
                    throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
                }
                userEntity.updatePassword(newPassword);

            } catch (Exception exception){
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_PASSWORD);
            }
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //문의사항
//    public void createQuestion(PostQuestionReq postQuestionReq) throws BaseException{
//        try{
//            //UserEntity userEntity = userRepository.findById(postUserImageReq.getUserIdx()).get();
//            //String image = postUserImageReq.getImage();
//
//            //userEntity.save(image);
//
//        }catch (Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}

package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;

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

    //시작페이지
    public String startPage(Long userIdx)throws BaseException{
        try{
            UserEntity user = userRepository.findById(userIdx).get();
            return user.getName();
        }catch (Exception e) {
            throw new BaseException(FAIL_START_PAGE);
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
                //인증 정보를 기반으로 JWT 토큰 생성
                TokenDto tokenDto = jwtService.createJwt(user.getId());
                String RT = tokenDto.getRefreshToken();

                //RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
//                redisTemplate.opsForValue()
//                        .set("RT:" + user.getId(), tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

                user.updateRT(RT);
                System.out.println("--------실행?????------------");
                return user.toPostLoginRes(tokenDto);
            }else {
                throw new BaseException(FAILED_TO_LOGIN);
            }
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    //Reissue: Token 정보 갱신
//    1. Access Token + Refresh Token 을 Request Body 에 받아서 검증합니다.
//    2. Refresh Token 의 만료 여부를 먼저 검사합니다.
//    3. Access Token 을 복호화하여 유저 정보 (Member ID) 를 가져오고 저장소에 있는 Refresh Token 과 클라이언트가 전달한 Refresh Token 의 일치 여부를 검사합니다.
//    4. 만약 일치한다면 로그인 했을 때와 동일하게 새로운 토큰을 생성해서 클라이언트에게 전달합니다.
//    5. Refresh Token 은 재사용하지 못하게 저장소에서 값을 갱신해줍니다.

    public TokenDto reissue(Reissue reissue)throws BaseException{
        //1. Refresh Token 검증
        if (!JwtService.validateToken(reissue.getRefreshToken())){
            throw new RuntimeException("refresh token이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 id 를 가져옵니다.
        int id = jwtService.getUserIdx();
        Long id2 = Long.valueOf(id);


        // 3. DB에서 2번 과정에서 가져온 id를 기반으로 Refresh Token값 찾아 가져옴
        UserEntity userEntity = userRepository.findById(id2).get();
        System.out.println("----------userEntity???--------------"+userEntity);
        String refreshToken = userEntity.getRT();

        System.out.println("----------실행?????---------refreshToken은??? "+refreshToken);

        // (추가) 로그아웃되어 DB 에 RefreshToken 이 존재하지 않는 경우 처리
        // 4. 가져온 Refresh Token값과 클라이언트 측으로부터 요청받은 Refresh Token값과 일치하는 지 검사
        if(refreshToken==null || refreshToken=="") {
            throw new BaseException(EMPTY_RT);
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            throw new BaseException(INVALID_RT);
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = jwtService.createJwt(id2);
        String newRT = tokenDto.getRefreshToken();
        System.out.println("---------새로운토큰 생성??-------newRT?? "+newRT);

        //6. RefreshToken Redis 업데이트
//        redisTemplate.opsForValue()
//                .set("RT:" + userEntity.getId(), tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        //userEntity.updateRT(tokenDto.getRefreshToken());

        //7. 토큰 발급
        return tokenDto;
    }

    //로그아웃
        public void logout(PostLogoutReq postLogoutReq)throws BaseException{
        try{
            // 1. Access Token 검증
            if (!jwtService.validateToken(postLogoutReq.getAccessToken())) {
                throw new RuntimeException("Access token이 유효하지 않습니다.");
            }

            // 2. Access Token 에서 id를 가져옵니다.
            int id = jwtService.getUserIdx();
            Long id2 = Long.valueOf(id);

            System.out.println("------userRepository.findById(id2)????-------"+userRepository.findById(id2));

            // 3. Redis 에서 해당 id로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
//            if (redisTemplate.opsForValue().get("RT:" + id2) != null) {
//                // Refresh Token 삭제
//                redisTemplate.delete("RT:" + id2);
//            }
            //3.DB에 해당 id로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            UserEntity userEntity = userRepository.findById(id2).get();
            String refreshToken = userEntity.getRT();
            if (refreshToken != null) {
                // Refresh Token 삭제
                userEntity.setRT(null);
            }
            // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
//            Long expiration = jwtService.getExpiration(postLogoutReq.getAccessToken());
//            redisTemplate.opsForValue()
//                    .set(postLogoutReq.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        } catch(Exception e){
            throw new BaseException(SERVER_ERROR);
        }
    }

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
    public void resetPassword(ResetPasswordReq resetPasswordReq)throws BaseException{
        try{
            //해당 아이디를 가진 userEntity찾기
            UserEntity userEntity = userRepository.findByUserId(resetPasswordReq.getUserId());

            //비밀번호 재설정
            String newPassword;
            try{
                try{
                    // 암호화: patchPasswordReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
                    newPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(resetPasswordReq.getNewPassword());
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

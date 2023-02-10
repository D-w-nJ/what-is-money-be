package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.goal.GoalRepository;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import jdk.internal.loader.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final RedisTemplate redisTemplate;

    //메일전송
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.file.path}")
    private String uploadFolder;

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
            System.out.println("-----------암호화 전 password-------"+postLoginReq.getPassword());
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postLoginReq.getPassword()); //암호화
            // 회원가입 할 때 비밀번호가 암호화되어 저장되었기 때문에 로그인 할 때도 암호화된 값끼리 비교를 해야함
        } catch (Exception e) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            UserEntity user = userRepository.findByUserId(postLoginReq.getUserId());

            //임시회원이라면
            if(user.getStatus()==1){
                if(user.getPassword().equals(password)){
                    //인증 정보를 기반으로 JWT 토큰 생성
                    TokenDto tokenDto = jwtService.createJwt(user.getId());
                    //db에 refresh토큰 저장
                    String refreshToken = tokenDto.getRefreshToken();
                    user.setRT(refreshToken);
//                     4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
//                    redisTemplate.opsForValue()
//                            .set("RT:" + user.getName(), tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

                    return user.toPostLoginRes(tokenDto);
                }
            }
            //일반회원이라면
            if (user.getPassword().equals(password)) { //비밀번호가 같다면
                //인증 정보를 기반으로 JWT 토큰 생성
                TokenDto tokenDto = jwtService.createJwt(user.getId());
                //db에 refresh토큰 저장
                String refreshToken = tokenDto.getRefreshToken();
                user.setRT(refreshToken);
                // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
//                redisTemplate.opsForValue()
//                        .set("RT:" + user.getName(), tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

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

    public ReissueRes reissue(ReissueReq reissueReq)throws BaseException{
        ReissueRes reissueRes;
        //1. Refresh Token 검증
        if (!JwtService.validateToken(reissueReq.getRefreshToken())){
            throw new RuntimeException("refresh token이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 id 를 가져옵니다.
        try{
            int id = jwtService.getUserIdx();
            Long id2 = Long.valueOf(id);
            // 3. DB에서 2번 과정에서 가져온 id를 기반으로 Refresh Token값 찾아 가져옴
            UserEntity userEntity = userRepository.findById(id2).get();
            System.out.println("----------userEntity???--------------"+userEntity);
            String refreshToken = userEntity.getRT();

            // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
            String refreshTokeninRedis = (String)redisTemplate.opsForValue().get("RT:" + userEntity.getName());
            System.out.println("=====RT from redis??======"+refreshTokeninRedis);

            System.out.println("----------실행?????---------refreshToken은??? "+refreshToken);

            // (추가) 로그아웃되어 DB 에 RefreshToken 이 존재하지 않는 경우 처리
            // 4. 가져온 Refresh Token값과 클라이언트 측으로부터 요청받은 Refresh Token값과 일치하는 지 검사
            if(refreshToken==null || refreshToken=="") {
                throw new BaseException(EMPTY_ACCESSTOKEN);
            }
            if(!refreshToken.equals(reissueReq.getRefreshToken())) {
                throw new BaseException(INVALID_RT);
            }

            //redis에서 확인
//            if(ObjectUtils.isEmpty(refreshToken)) {
//                throw new BaseException(EMPTY_ACCESSTOKEN);
//            }
//            if(!refreshToken.equals(reissueReq.getRefreshToken())) {
//                throw new BaseException(INVALID_RT);
//            }

            // 5. 새로운 토큰 생성(access토큰과 refresh토큰 둘 다 생성)
            TokenDto tokenDto = jwtService.createJwt(id2);
            String newAccessToken = tokenDto.getAccessToken();
            Long accessTokenExpiration = tokenDto.getAccessTokenExpirationTime();
            System.out.println("---------새로운토큰 생성??-------newAccessToken?? "+newAccessToken);
            //Long expiration = jwtService.getExpiration(newAccessToken);

            //6. db에 RefreshToken 업데이트
            String newRefreshToken = tokenDto.getRefreshToken();
            userEntity.updateRT(newRefreshToken);

            //redis에 RefreshToken 업데이트
//            redisTemplate.opsForValue()
//                    .set("RT:" + userEntity.getName(), tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

            //7. 토큰 발급(access토큰과 access토큰 만료시간 반환)
            reissueRes = new ReissueRes(newAccessToken, accessTokenExpiration);

        }catch(Exception ignored){
            throw new BaseException(EMPTY_ACCESSTOKEN);
        }

        return reissueRes;
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
            System.out.println("=========id???=========="+id);
            Long id2 = Long.valueOf(id);

            System.out.println("----------------id2????-------"+id2);

            //3.DB에 해당 id로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            UserEntity userEntity = userRepository.findById(id2).get();
            String refreshToken = userEntity.getRT();
            if (refreshToken != null) {
                // Refresh Token 삭제
                userEntity.setRT(null);
            }
            // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
//            if (redisTemplate.opsForValue().get("RT:" + userEntity.getName()) != null) {
//                // Refresh Token 삭제
//                redisTemplate.delete("RT:" +userEntity.getName());
//            }

            // 4. 해당 Access Token 유효시간 가지고 와서 BlackList로 저장하기
            Long expiration = jwtService.getExpiration(postLogoutReq.getAccessToken());
            System.out.println("=========expiration======"+expiration);
            redisTemplate.opsForValue()
                    .set(postLogoutReq.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

            System.out.println("============여기까진 완료============");
            System.out.println("=============블랙리스트 등록완료~================"+redisTemplate.opsForValue().get(postLogoutReq.getAccessToken()));
            //redisUtil.setBlackList(postLogoutReq.getAccessToken(), "access_token", expiration);
            //postLogoutReq.setAccessToken("logout");

        } catch(Exception e){
            throw new BaseException(SERVER_ERROR);
        }
    }



    //유저정보수정_아이디_현재아이디가져오기
    public String getUserId(Long userIdx)throws BaseException{
        try{
            UserEntity userEntity = userRepository.findById(userIdx).get();
            String userId = userEntity.getUserId();
            return userId;
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저정보수정_아이디
    public void modifyUserId(PatchUserIdReq patchUserIdReq)throws BaseException{

        try{
            System.out.println("------------userService는 됨---------");
            String newUserId = patchUserIdReq.getNewUserId();
            Long userIdx = patchUserIdReq.getUserIdx();

            UserEntity userEntity = userRepository.findById(userIdx).get();
            System.out.println("-----------userEntity----------"+userEntity);

            userEntity.updateUserId(newUserId);
            System.out.println("---------newUserId--------"+newUserId);
//            System.out.println("userService이다. userRepository가기 전. 여기까진 되었나?!");

//            userRepository.updateUserId(newUserId, userIdx);

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
            userEntity.setStatus(0);
            //userRepository.updateUserId(newUserId, userIdx);

        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.MODIFY_FAIL_PASSWORD);
        }
    }
    //회원탈퇴
    public void deleteUser(Long userIdx)throws BaseException{
        try{
            userRepository.deleteById(userIdx);
            System.out.println("=======실행되었나?=======");

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
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
    //프로필이미지 불러오기
//    public byte[] getUserImage(Long userIdx)throws BaseException{
//        try{
//            UserEntity user = userRepository.findById(userIdx).get();
//            System.out.println("------------userEntity-----------"+user);
//            String imageName = user.getImage();
//            System.out.println("image??"+imageName);
//
//            if(imageName==null){
//                return null;
//            }else{
//                //이미지를 불러오기
//                //해당 경로의 image를 FileInputstream의 객체를 만들어서
//                //byte[] 형태의 값으로 incoding 후 보내게 된다
//                System.out.println("여기서???"+uploadFolder);
//                InputStream imageStream = new FileInputStream(uploadFolder+"/"+imageName);
//                System.out.println("=============imageStream????====="+imageStream);
//                byte[] imageByteArray = IOUtils.toByteArray(imageStream);
//                imageStream.close();
//
//                System.out.println("================imageByteArray???======"+imageByteArray);
//                return imageByteArray;
//
//            }
//
//        }catch (Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

    //프로필설정_이름, 아이디, 이미지 불러오기
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
    public void findUserId(FindUserIdReq findUserIdReq)throws BaseException{
        String name;
        String userId;
        try{
            //해당 이메일을 가진 userEntity찾기
            try{
                UserEntity userEntity = userRepository.findByEmail(findUserIdReq.getEmail());
                System.out.println("-------------해당이메일이 있느냐?------------"+userEntity);
                //userEntity에서 name과 userId 추출 후 해당이메일로 보내기
                name = userEntity.getName();
                userId = userEntity.getUserId();
            }catch (Exception e){
                throw new BaseException(INVALID_EMAIL);
            }

            System.out.println("---------name & userId--------"+name+"     "+userId);

            //네이버
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            mimeMessageHelper.setTo(findUserIdReq.getEmail());
//            mimeMessageHelper.setSubject("[머니뭐니] "+ name +"님의 아이디를 보내드립니다.");
//            mimeMessageHelper.setText(name+"님의 아이디: "+userId);
//
//            javaMailSender.send(mimeMessage);


            //이메일보내기
            MailHandler mailHandler = new MailHandler(javaMailSender);
            mailHandler.setFrom(from); //이거 naver메일 보낼때는 필수다!!!!!!
            mailHandler.setTo(findUserIdReq.getEmail());
            mailHandler.setSubject("[머니뭐니] "+ name +"님의 아이디를 보내드립니다.");
            mailHandler.setText(name+"님의 아이디: "+userId);
            mailHandler.send();

            System.out.println("-------메일 발송이 되었느냐??--------");
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    //랜덤함수로 임시비밀번호 구문 만들기
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    //비밀번호 찾기
    public void findPassword(FindPasswordReq findPasswordReq)throws BaseException{
        try{
            UserEntity userEntity = userRepository.findByUserId(findPasswordReq.getUserId());
            String name = userEntity.getName();

            //임시비밀번호 발급 후 전달(status=1). 이때 status==1이면 무조건 비밀번호를 갱신해야 사용할 수 있도록
            String tmpPassword = getTempPassword();
            //임시비밀번호 암호화
            String tmpPassword2;
            try{
                // 암호화: patchPasswordReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
                tmpPassword2 = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(tmpPassword);
            } catch (Exception ignored){
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }
            userEntity.updatePassword(tmpPassword2);
            userEntity.setStatus(1);//1은 임시상태
            //해당 아이디가 존재하면 해당 이메일로 메일보내기
            MailHandler mailHandler = new MailHandler(javaMailSender);
            mailHandler.setTo(findPasswordReq.getEmail());
            mailHandler.setSubject("[머니뭐니] "+ name +"님의 임시비밀번호 안내 이메일입니다.");
            mailHandler.setText(name+"님, 안녕하세요. 머니뭐니 임시비밀번호 안내 관련 이메일 입니다." + " 회원님의 임시 비밀번호는 "
                    + tmpPassword + " 입니다." + "로그인 후에 비밀번호를 변경을 해주세요");
            //mailHandler.setFrom("머니뭐니 주식회사");
            mailHandler.send();

        }catch (Exception exception){
            throw new BaseException(INVALID_USER_ID);
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

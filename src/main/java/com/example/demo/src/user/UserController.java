package com.example.demo.src.user;

import com.example.demo.config.BaseResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
// @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
//  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
//  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
// @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/users")
// method가 어떤 HTTP 요청을 처리할 것인가를 작성한다.
// 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
// URL(/app/users)을 컨트롤러의 메서드와 매핑할 때 사용
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class UserController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    private final UserService userService;
    private final UserRepository userRepository;


    public UserController(JwtService jwtService, UserService userService,
                          UserRepository userRepository) {
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    @PostMapping("/signup")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createUser(@Valid @RequestBody PostUserReq postUserReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
//         email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        //아이디 중복체크 안하고 회원가입완료버튼 누르면 중복확인 해주세요!! 문구 뜨게하기
        if(postUserReq.isIdCheck() == false){
            return new BaseResponse<>(CHECK_USER_ID);
        }

        String pw2 = postUserReq.getConfirmPassword();

        //비밀번호중복확인(비밀번호 2번입력 확인차)
        if(!postUserReq.getPassword().equals(pw2)){
            return new BaseResponse<>(INVALID_USER_PASSWORD);
        }


        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 아이디중복확인 API
     * [GET] /users/idCheck
     * */
    @GetMapping("/idCheck/{userId}")
    public BaseResponse<String> CheckId(@PathVariable("userId") String userId){
        String result;
        try{
            if(userId==null || userId==""){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            boolean duplicate = userService.CheckId(userId);
            System.out.println("------------실행?------------duplicate?? "+duplicate);
            if(duplicate == true){
                result = "이미 사용중인 아이디입니다.";
            }else{
                result = "사용가능한 아이디입니다!";
            }
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 시작페이지 API
     * [GET] /users/start
     * */
    @ResponseBody
    @GetMapping("/start/{userIdx}")
    public BaseResponse<String> startPage(@PathVariable("userIdx") Long userIdx){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            String name = userService.startPage(userIdx);
            String result = name+"님, 어서오세요!";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@Valid @RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**{
     * Reissue
     * [POST] /users/reissue
     * */
    @ResponseBody
    @PostMapping("/reissue")
    public BaseResponse<ReissueRes> reissue(@Valid @RequestBody ReissueReq reissueReq) {
        try{
            ReissueRes reissueRes = userService.reissue(reissueReq);
            return new BaseResponse<>(reissueRes);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그아웃 API
     * [POST] /users/logout
     * */
    @ResponseBody
    @PostMapping("/logout")
    public BaseResponse<String> logout(@Valid @RequestBody PostLogoutReq postLogoutReq) {
        try{
            userService.logout(postLogoutReq);

            String result = "로그아웃 완료!!";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보수정_아이디_현재아이디가져오기 API
     * [GET] /users/{userIdx}
     * */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<String> getUserId(@PathVariable("userIdx") Long userIdx){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            String userId = userService.getUserId(userIdx);
            return new BaseResponse<>(userId);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/modifyUserId
     */
    @ResponseBody
    @PatchMapping("/modifyUserId")
    public BaseResponse<String> modifyUserId(@Valid @RequestBody PatchUserIdReq patchUserIdReq) {
        String result;
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != patchUserIdReq.getUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            System.out.println("--------------------------------");
            int userIdxByJwt = jwtService.getUserIdx();
            //Long으로 타입 변환
            //Long userIdxByJwt = Long.valueOf(userIdxByJwt1);
            if (patchUserIdReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
//            //아이디, userIdx notEmpty
//            if(patchUserIdReq.getUserIdx()==null){
//                return new BaseResponse<>(REQUEST_ERROR);
//            }
//            if(patchUserIdReq.getNewUserId()==null || patchUserIdReq.getNewUserId()==""){
//                return new BaseResponse<>(USERS_EMPTY_USER_ID);
//            }

            //아이디중복확인
            if(patchUserIdReq.isIdCheck()==false){
                return new BaseResponse<>(CHECK_USER_ID);
            }else{
                //같다면 유저네임 변경
                userService.modifyUserId(patchUserIdReq);
                result = "회원정보가 수정되었습니다.";
            }


            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/modifyPassword")
    public BaseResponse<String> modifyPassword(@Valid @RequestBody PatchPasswordReq patchPasswordReq) {
        String result;
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != patchPasswordReq.getUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            System.out.println("---------------컨트롤러의 시작------------------");
            int userIdxByJwt = jwtService.getUserIdx();
            if (patchPasswordReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            System.out.println("----------------------------------");
            //비밀번호중복확인
            if(!patchPasswordReq.getNewPassword().equals(patchPasswordReq.getConfirmNewPassword())){
                System.out.println("비밀번호가 다릅니다. 다시 확인 부탁");
                return new BaseResponse<>(INVALID_USER_PASSWORD);
            }else{
                //같다면 유저비밀번호 변경
                System.out.println("--------------바로 여기로 넘어가나?_---------------------");
                userService.modifyPassword(patchPasswordReq);
                result = "회원정보가 수정되었습니다.";
            }
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원탈퇴 API
     * [DELETE] /users/deleteUser
     */
    @ResponseBody
    @DeleteMapping("/deleteUser")
    public BaseResponse<String> deleteUser(@RequestBody DeleteUserReq deleteUserReq) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != deleteUserReq.getUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            userService.deleteUser(deleteUserReq);

            String result = "회원정보가 탈퇴되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원정보설정_이미지 API
     * [POST] /users/profile/{userIdx}/{file}
     */
    @ResponseBody
    @PostMapping("/profile")
    public BaseResponse<String> postUserImage(PostUserImageReq postUserImageReq) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != postUserImageReq.getUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            userService.postUserImage(postUserImageReq);

            String result = "프로필 사진이 정상적으로 업로드되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 프로필설정_이름, 아이디, 이미지 불러오기 API
     * [GET] /users/profile/{userIdx}
     * */
    @ResponseBody
    @GetMapping("/profile/{userIdx}")
    public BaseResponse<GetUsersProfileRes> getUsers(@PathVariable("userIdx") Long userIdx){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            GetUsersProfileRes getUsersProfileRes = userService.getUsers(userIdx);
            return new BaseResponse<>(getUsersProfileRes);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원정보설정_알림
     * [POST] /users/alarm
     * */
    @ResponseBody
    @PostMapping("/alarm")
    public BaseResponse<String> postUserAlarm(@RequestBody PostUserAlarmReq postUserAlarmReq){
        String result;
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != postUserAlarmReq.getUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            userService.postUserAlarm(postUserAlarmReq);

            if(postUserAlarmReq.isAlarm()==true){
                result = "알림을 켭니다.";
            }else{
                result = "알림을 끕니다.";
            }

            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 아이디찾기 API
     * [POST] /users/findUserId
     * */
    @ResponseBody
    @PostMapping("/findUserId")
    public BaseResponse<String> findUserId(@Valid @RequestBody FindUserIdReq findUserIdReq){
        try {
            userService.findUserId(findUserIdReq);
            System.out.println("-----------실행??------------");

            String result = "메일이 성공적으로 발송되었습니다!";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**비밀번호찾기 API
     * [POST] /users/findPassword
     * */
    @ResponseBody
    @PostMapping("/findPassword")
    public BaseResponse<String> findPassword(@Valid @RequestBody FindPasswordReq findPasswordReq){
        String result;
        try{
            UserEntity userEntity = userRepository.findByUserId(findPasswordReq.getUserId());
            try{
                //해당아이디가 존재하는지 확인
                String name = userEntity.getName();
            }catch (Exception e){
                throw new BaseException(INVALID_USER_ID);
            }
            //해당 이메일이 존재하는지 확인
            String email = userEntity.getEmail();
            if(email.equals(findPasswordReq.getEmail())==false){
                System.out.println("이 이메일은 등록된 정보가 없다이말이야~~제발멈춰~~~~");
                return new BaseResponse<>(INVALID_EMAIL);
            }else{
                userService.findPassword(findPasswordReq);
                result = "이메일로 비밀번호를 재설정할 수 있는 메일이 발송되었습니다.";
            }

            return new BaseResponse<>(result);

        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 비밀번호 재설정 API
     * [PATCH] /users/resetPassword
     * */
    @ResponseBody
    @PatchMapping("/resetPassword")
    public BaseResponse<String> resetPassword(@Valid @RequestBody ResetPasswordReq resetPasswordReq){
        try {
            //아이디, 비번 not Empty
            if(resetPasswordReq.getUserId()==null || resetPasswordReq.getUserId()==""){
                return new BaseResponse<>(REQUEST_ERROR);
            }
            if(resetPasswordReq.getNewPassword()==null || resetPasswordReq.getNewPassword()==""){
                return new BaseResponse<>(REQUEST_ERROR);
            }
            if(resetPasswordReq.getConfirmNewPassword()==null || resetPasswordReq.getConfirmNewPassword()==""){
                return new BaseResponse<>(REQUEST_ERROR);
            }

            //비밀번호 중복확인
            if(!resetPasswordReq.getNewPassword().equals(resetPasswordReq.getConfirmNewPassword())){
                return new BaseResponse<>(INVALID_USER_PASSWORD);
            }
            //유저비밀번호 변경
            userService.resetPassword(resetPasswordReq);

            String result = "비밀번호를 재설정하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 문의사항 API
     * [POST] /users/question
     */
//    @ResponseBody
//    @PostMapping("/question")
//    public BaseResponse<String> createQuestion(@RequestBody PostQuestionReq postQuestionReq) {
//        if (postQuestionReq.getEmail() == null) {
//            return new BaseResponse(POST_USERS_EMPTY_EMAIL);
//        }
//        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
//        if (!isRegexEmail(postQuestionReq.getEmail())) {
//            return new BaseResponse(POST_USERS_INVALID_EMAIL);
//        }
//        try {
//            //PostUserRes postUserRes = userService.createUser(postUserReq);
//            userService.createQuestion(postQuestionReq);
//
//            String result = "문의사항이 정상적으로 접수되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
}
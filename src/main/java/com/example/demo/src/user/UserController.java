package com.example.demo.src.user;

import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.question.model.PostQuestionReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;


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


    public UserController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
        this.userService = userService;
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
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

        //아이디, 이름 notEmpty
        if(postUserReq.getUserId()==""){
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(postUserReq.getName() == ""){
            return new BaseResponse<>(USERS_EMPTY_USER_NAME);
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
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            if(postLoginReq.getUserId()==""){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(postLoginReq.getPassword()==""){
                return new BaseResponse<>(INVALID_USER_PASSWORD);
            }
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그아웃 API
     * [POST] /users/logout
     * */
//    @ResponseBody
//    @PostMapping("/logout")
//    public BaseResponse<PostLogoutRes> logout(@RequestBody PostLogoutReq postLogoutReq) {
//        try{
//            PostLogoutRes postLogoutRes = userService.logout(postLogoutReq);
//            return new BaseResponse<>(postLogoutRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//
//    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/modifyUserId
     */
    @ResponseBody
    @PatchMapping("/modifyUserId")
    public BaseResponse<String> modifyUserId(@RequestBody PatchUserIdReq patchUserIdReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (patchUserIdReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            //아이디중복확인
            if(patchUserIdReq.isIdCheck()==false){
                return new BaseResponse<>(CHECK_USER_ID);
            }

            //같다면 유저네임 변경
            userService.modifyUserId(patchUserIdReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/modifyPassword")
    public BaseResponse<String> modifyPassword(@RequestBody PatchPasswordReq patchPasswordReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (patchPasswordReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            //비밀번호중복확인
            if(!patchPasswordReq.getNewPassword().equals(patchPasswordReq.getConfirmNewPassword())){
                return new BaseResponse<>(INVALID_USER_PASSWORD);
            }
            //같다면 유저비밀번호 변경
            userService.modifyPassword(patchPasswordReq);

            String result = "회원정보가 수정되었습니다.";
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
     * [POST] /users/profile
     */
    @ResponseBody
    @PostMapping("/profile")
    public BaseResponse<String> postUserImage(@RequestBody PostUserImageReq postUserImageReq) {
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
     * 회원정보설정_알림
     * [POST] /users/alarm
     * */
    @ResponseBody
    @PostMapping("/alarm")
    public BaseResponse<String> postUserAlarm(@RequestBody PostUserAlarmReq postUserAlarmReq){
        String result;
        try{
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
    public BaseResponse<String> findUserId(@RequestBody FindUserIdReq findUserIdReq){
        if (findUserIdReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(findUserIdReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            String userId = userService.findUserId(findUserIdReq);
            return new BaseResponse<>(userId);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**비밀번호찾기 API
     * [POST] /users/findPassword
     * */
    @ResponseBody
    @PostMapping("/findPassword")
    public BaseResponse<String> findPassword(@RequestBody FindPasswordReq findPasswordReq){
        if(findPasswordReq.getUserId() == null){
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if (findPasswordReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(findPasswordReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        String result = "이메일로 비밀번호를 재설정할 수 있는 메일이 발송되었습니다.";
        return new BaseResponse<>(result);
    }

    /**
     * 비밀번호 재설정 API
     * [PATCH] /users/resetPassword
     * */
    @ResponseBody
    @PatchMapping("/resetPassword")
    public BaseResponse<String> resetPassword(@RequestBody ResetPassword resetPassword){
        try {
            //유저비밀번호 변경
            userService.resetPassword(resetPassword);

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
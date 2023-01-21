package com.example.demo.src.user;

import com.example.demo.src.goal.model.GetGoalRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 아이디중복확인
     * [GET] /users/idCheck
     * */
    @ResponseBody
    @GetMapping("/idCheck/{id_str}")
    public ResponseEntity<Boolean> checkIdDuplicate(@PathVariable String id_str){
        return ResponseEntity.ok(userService.checkIdDuplicate(id_str));
    }
//    @ResponseBody
//    @GetMapping("/idCheck/{id_str}")
//    public BaseResponse<GetIdCheckRes> idCheck(@PathVariable("id_str") String id_str){
//        try{
//            GetIdCheckRes getIdCheckRes = userService.getIdCheckRes(id_str);
//            if(getIdCheckRes.getId_str() == id_str){
//                return new BaseResponse<>(DUPLICATED_ID);
//            }else{
//                return new BaseResponse<>(SUCCESS);
//            }
//
//        } catch(BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//
//    }

//    public BaseResponse<List<GetIdCheckRes>> idCheck(@PathVariable("id") String id_str){
//        try{
//            List<GetIdCheckRes> getIdCheckRes = userService.idCheck(id_str);
//            return new BaseResponse<>(getIdCheckRes);
//        } catch (BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}")
//    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
//        try {
///**
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//  **************************************************************************
// */
//            int userIdxByJwt = jwtService.getUserIdx();
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickname());
//            userService.modifyUserName(patchUserReq);
//
//            String result = "회원정보가 수정되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
}

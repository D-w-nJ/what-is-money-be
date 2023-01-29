package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    INVALID_RT(false, 2004, "Refresh Token 정보가 일치하지 않습니다."),
    EMPTY_RT(false, 2005, "로그아웃된 사용자입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    INVALID_USER_PASSWORD(false, 2011, "비밀번호를 다시 입력하세요."),
    USERS_EMPTY_USER_NAME(false, 2012, "이름을 입력해주세요"),
    USERS_EXISTS_USER_ID(false, 2013, "이미 사용중인 아이디입니다."),
    CHECK_USER_ID(false, 2014, "아이디 중복확인을 해주세요."),
    FAIL_START_PAGE(false, 2015, "메인페이지 시작 실패"),
    INVALID_USER_ID(false, 2016, "존재하지 않는 아이디"),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    INVALID_EMAIL(false, 2018, "가입하신 적이 없는 이메일입니다."),

    // [POST] /category
    POST_CATEGORY_EMPTY_NAME(false, 2018, "카테고리 명을 입력해주세요."),
    POST_CATEGORY_EXISTS_NAME(false, 2019, "중복된 카테고리입니다."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERID(false,4014,"유저아이디 수정 실패"),
    MODIFY_FAIL_PASSWORD(false, 4015, "유저비밀번호 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

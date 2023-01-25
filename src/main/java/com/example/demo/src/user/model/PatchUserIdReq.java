package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor
public class PatchUserIdReq {
    private Long userIdx;
    @NotEmpty(message = "아이디를 입력해주세요")
    private String newUserId;
    @NotEmpty(message = "아이디중복확인을 해주세요")
    private boolean idCheck;
}

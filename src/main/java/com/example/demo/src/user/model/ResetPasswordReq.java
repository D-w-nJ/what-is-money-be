package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor
public class ResetPasswordReq {
    private String userId;
    private String newPassword;
    @NotEmpty(message = "비밀번호를 한번 더 입력해주세요")
    private String confirmNewPassword;
}

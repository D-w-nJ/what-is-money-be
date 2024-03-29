package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, jwt)를 받는 생성자를 생성
public class GetUsersProfileRes {
    private String name;
    private String userId;
    //이미지는 byte 배열 형태를 띄고 있다.
    private byte[] image;
}

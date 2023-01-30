package com.example.demo.src.user.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Member;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(email, password, nickname, profileImage)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUserImageReq {
    private Long userIdx;
    private MultipartFile image;
    public UserEntity toEntity(String memberImgUrl) {
        return UserEntity.builder()
                .image(memberImgUrl)
                .build();
    }
}

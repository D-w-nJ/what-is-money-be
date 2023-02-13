package com.example.demo.src.question.model;

import com.example.demo.src.user.model.UserEntity;
import jdk.vm.ci.meta.Local;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(email, password, nickname, profileImage)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
public class PostQuestionReq {
    private Long userIdx;
    private String email;
    private String content;
    private String createdAt;

    public QuestionEntity toEntity(UserEntity user) {
        String strDate = createdAt.substring(0,19);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime reDate = LocalDateTime.parse(strDate,format);
        return QuestionEntity.builder()
                .email(email)
                .content(content)
                .createdAt(reDate)
                .user(user)
                .build();
    }
}

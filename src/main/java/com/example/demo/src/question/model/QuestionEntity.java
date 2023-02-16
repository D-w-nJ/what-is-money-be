package com.example.demo.src.question.model;

import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.record.model.PostRecordRes;
import com.example.demo.src.user.model.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity //JPA가 사용하는 객체라는 뜻이다. 이 어노테이션이 있어야 JPA가 인식할 수 있다.
@Table(name = "question")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class QuestionEntity {
    @Id //테이블의 PK와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long id;
    private String email;
    private String content;
    private LocalDateTime createdAt;
    //private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;



    public PostQuestionRes toPostQuestionRes() {
        return new PostQuestionRes(user.getId(),
                createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                id);
    }

}

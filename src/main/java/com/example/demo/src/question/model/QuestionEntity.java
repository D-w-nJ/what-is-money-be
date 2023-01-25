package com.example.demo.src.question.model;

import com.example.demo.src.user.model.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity //JPA가 사용하는 객체라는 뜻이다. 이 어노테이션이 있어야 JPA가 인식할 수 있다.
@Table(name = "question")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Data
public class QuestionEntity {
    @Id //테이블의 PK와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long id;
    private String email;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    public void save(String email, String content, LocalDateTime createdAt){
        this.email = email;
        this.content = content;
        this.createdAt = createdAt;
    }
}

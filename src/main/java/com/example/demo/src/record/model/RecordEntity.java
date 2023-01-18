package com.example.demo.src.record.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity //JPA가 사용하는 객체라는 뜻이다. 이 어노테이션이 있어야 JPA가 인식할 수 있다.
@Table(name = "record")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
public class RecordEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;
    private boolean flag; //0:저축, 1: 지출
    private LocalDateTime date;

    @ManyToOne @JoinColumn(name="category_id",referencedColumnName = "id")
    private CategoryEntity category;
    @ManyToOne @JoinColumn(name="user_id", referencedColumnName = "id")
    private UserEntity user;
    @ManyToOne @JoinColumn(name="goal_id",referencedColumnName = "id")
    private GoalEntity goal;

    public PostRecordRes toPostRecordRes() {

        return new PostRecordRes(user.getId(),goal.getId(),
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                category.getCategoryIdx());
    }
}

package com.example.demo.src.record.model;

import com.example.demo.src.category.model.CategoryEntity;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.user.model.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
public class PostRecordReq {

    private Long userIdx;
    private Long goalIdx;
    private String date;
    private Long category;
    private int amount;

    public RecordEntity toEntity(UserEntity user, GoalEntity goal, CategoryEntity category) {
        String strDate = date.substring(0,19);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime reDate = LocalDateTime.parse(strDate,format);
        return RecordEntity.builder()
                .amount(amount)
                .date(reDate)
                .user(user)
                .goal(goal)
                .category(category)
                .build();
    }
}

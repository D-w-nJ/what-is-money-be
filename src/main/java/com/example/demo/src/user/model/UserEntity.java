package com.example.demo.src.user.model;

import com.example.demo.src.goal.model.GoalEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity //JPA가 사용하는 객체라는 뜻이다. 이 어노테이션이 있어야 JPA가 인식할 수 있다.
@Table(name = "user")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Data
public class UserEntity {

    @Id //테이블의 PK와 해당 필드를 매핑한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다.
    private Long id;

    //@Column을 사용할 경우 객체의 필드와 테이블의 컬럼을 매핑한다.
    //아래와 같이 @Column을 생략할 경우 필드의 이름을 테이블 컬럼 이름으로 사용한다.
    @Column(name = "id_str")
    private String userId;

    private String password;
    private String name;

    @Column(unique = true)
    private String email;
    private boolean agree;
    private boolean alarm;
    private int status;
    private String image;

    //refresh token db에 저장
    @Column(name = "refresh_token")
    private String RT;

    @OneToMany(mappedBy = "user_id")
    private List<GoalEntity> goalEntityList = new ArrayList<>();

    /*
     @OneToMany(mappedBy = "category_id")
    private List<GoalEntity> goalEntities = new ArrayList<>();
     */

    // 회원가입 (entity->DTO)
    public PostUserRes toPostUserRes() {
        return new PostUserRes(id);
    }

    // 로그인 (entity -> DTO)
    public PostLoginRes toPostLoginRes(TokenDto tokenDto) {
        return new PostLoginRes(id, tokenDto);
    }


    public void updateUserId(String newUserId){
        this.userId = newUserId;
    }
    public void updatePassword(String newPassword){
        this.password = newPassword;
    }
    public void saveImage(String image){
        this.image = image;
    }
    public void saveAlarm(boolean alarm){
        this.alarm = alarm;
    }
    public void updateRT(String RT){
        this.RT = RT;
    }
}

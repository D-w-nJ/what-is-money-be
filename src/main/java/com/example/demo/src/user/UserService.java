package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
//        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
//            throw new BaseException(POST_USERS_EXISTS_EMAIL);
//        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword()); // 암호화코드
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            UserEntity userEntity = postUserReq.toEntity();   // DTO -> Entity 변환
            userRepository.save(userEntity);   // 유저 DB에 저장
            String jwt = jwtService.createJwt(userEntity.getId());
            return userEntity.toPostUserRes(jwt);    // Entity -> DTO 변환
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //로그인(password 검사)
    public PostLoginRes login(PostLoginReq postLoginReq)throws BaseException {

        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postLoginReq.getPassword()); //암호화
            // 회원가입 할 때 비밀번호가 암호화되어 저장되었기 때문에 로그인 할 때도 암호화된 값끼리 비교를 해야함
        } catch (Exception e) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            UserEntity user = userRepository.findByEmail(postLoginReq.getEmail());
            user.setPassword(password);
            if (user.getPassword().equals(password)) { //비밀번호가 같다면
                String jwt = jwtService.createJwt(user.getId());
                return user.toPostLoginRes(jwt);
            } else {
                throw new BaseException(FAILED_TO_LOGIN);
            }
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }
}

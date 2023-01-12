package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceV2 {

    private final JpaUserRepository jpaUserRepository;

    public List<UserEntity> getUsers() throws BaseException {
        try {
            return jpaUserRepository.findAll();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public UserEntity getUser(Long userIdx) throws BaseException {
        try {
            return jpaUserRepository.findById(userIdx).orElseThrow(()->new BaseException(DATABASE_ERROR));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

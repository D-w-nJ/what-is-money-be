package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.src.category.CategoryRepository;
import com.example.demo.src.goal.GoalRepository;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.record.model.*;
import com.example.demo.src.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final CategoryRepository categoryRepository;

    public PostRecordRes createRecord(PostRecordReq postRecordReq) throws BaseException {
        try {
            RecordEntity record = postRecordReq.toEntity(
                    userRepository.findById(postRecordReq.getUserIdx()).orElse(null),
                    goalRepository.findById(postRecordReq.getGoalIdx()).orElse(null),
                    categoryRepository.findById(postRecordReq.getCategory()).orElse(null));
            recordRepository.save(record);
            return record.toPostRecordRes();
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public DeleteRecordRes deleteRecord(DeleteRecordReq deleteRecordReq) throws BaseException {
        try {
            recordRepository.deleteById(deleteRecordReq.getRecordIdx());
            return new DeleteRecordRes(deleteRecordReq.getUserIdx());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

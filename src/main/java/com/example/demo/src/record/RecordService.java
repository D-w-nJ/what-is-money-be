package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.record.model.*;
import com.example.demo.src.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

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
                    userRepository.findById(postRecordReq.getUserIdx()),
                    goalRepository.findById(postRecordReq.getGoalIdx()),
                    categoryRepository.findById(postRecordReq.getCategory()));
            recordRepository.save(record);
            return record.toPostRecordRes();
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public DeleteRecordRes deleteRecord(DeleteRecordReq deleteRecordReq) throws BaseException {
        try {
            RecordEntity record = recordRepository.findById(deleteRecordReq.getRecordIdx()).orElse(null);
            GoalEntity goal = goalRepository.findById(record.getId());
            recordRepository.deleteById(deleteRecordReq.getRecordIdx());
            Long numOfRecordLeft = recordRepository.CountByUserAndGoal(deleteRecordReq.getUserIdx(),goal.getId());
            return new DeleteRecordRes(deleteRecordReq.getUserIdx(), numOfRecordLeft);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
